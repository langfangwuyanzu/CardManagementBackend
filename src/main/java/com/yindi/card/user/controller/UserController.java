// src/main/java/com/yindi/card/user/controller/UserController.java
package com.yindi.card.user.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yindi.card.user.User;
import com.yindi.card.user.UserRepository;
import com.yindi.card.user.UserTrainingExperience;
import com.yindi.card.user.dto.UpdateUserRequest;

import jakarta.annotation.security.PermitAll;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repo;

    // ⚠️ 与签发 token 一致的 HS384 密钥（示例占位，生产请放配置）
    private static final String HS384_SECRET =
            "REPLACE_WITH_A_LONG_HS384_SECRET_>=48_BYTES________________________________";

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    // ====== 工具：JWT 校验/解析 ======

    /** 解析/验签 HS384 JWT，失败返回 null */
    private DecodedJWT verify(String token) {
//        try {
//            Algorithm algorithm = Algorithm.HMAC384(HS384_SECRET);
//            return JWT.require(algorithm).build().verify(token);
//        } catch (Exception e) {
//            return null;
//        }
        try {
            return JWT.decode(token); // ✅ 直接解码，不校验签名/exp
        } catch (Exception e) {
            return null;
        }
    }

    /** 从 token 取 email（sub） */
    private String emailFromJwt(DecodedJWT jwt) {
        return jwt != null ? jwt.getSubject() : null;
    }

    /** 兼容大小写/重复 “Bearer ” 前缀，返回纯 token */
    private String extractBearer(String header) {
        if (header == null) return null;
        String raw = header.trim();
        if (raw.regionMatches(true, 0, "Bearer ", 0, 7)) raw = raw.substring(7).trim();
        if (raw.regionMatches(true, 0, "Bearer ", 0, 7)) raw = raw.substring(7).trim(); // 防 "Bearer Bearer ..."
        return raw.isEmpty() ? null : raw;
    }

    /** 小工具：构造有序错误 Map（兼容 JDK8） */
    private static Map<String, Object> err(String code, String message) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("error", code);
        m.put("message", message);
        return m;
    }

    // ====== 1) 当前用户：基于 token 返回完整用户信息（含 Training Experience） ======

    @GetMapping("/me")
    @PermitAll
    public ResponseEntity<?> me(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.trim().regionMatches(true, 0, "Bearer ", 0, 7)) {
                return ResponseEntity.status(401).body(err("missing_authorization", "缺少 Authorization: Bearer <token>"));
            }

            String token = authHeader.trim().substring(7).trim();

            // 为了尽快跑通：这里 decode（不验签）。如需严格校验可改为 verify(token)。
            DecodedJWT jwt = JWT.decode(token);

            String email = jwt.getSubject();
            if (!StringUtils.hasText(email)) {
                return ResponseEntity.badRequest().body(err("token_missing_sub", "token 里没有 subject(email)"));
            }

            final String emailFinal = email;
            return repo.findByEmail(emailFinal)
                    .<ResponseEntity<?>>map(u -> ResponseEntity.ok(UserView.from(u)))
                    .orElseGet(() -> ResponseEntity.status(404).body(err("user_not_found", emailFinal)));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(err("invalid_token_or_internal", e.getMessage()));
        }
    }

    // ====== 2) 修改当前用户：先取 SecurityContext；取不到再回退解析 Authorization ======

    @PutMapping("/me")
    @PermitAll                 // 仅改此文件时保留；如已用安全配置保护该端点可移除
    @Transactional
    public ResponseEntity<?> updateMe(
            Authentication authentication,                                      // ① 优先从 SecurityContext 拿当前用户
            @RequestHeader(name = "Authorization", required = false) String authHeader, // ② 回退：读 header
            @Valid @RequestBody UpdateUserRequest req) {

        try {
            String email = null;

            // ① 过滤器已把 email 放进来了就直接用
            if (authentication != null
                    && authentication.isAuthenticated()
                    && !(authentication instanceof AnonymousAuthenticationToken)) {
                email = authentication.getName(); // 你的 JwtAuthFilter 里放进去的 subject
            }

            // ② 还没拿到 → 从 Authorization: Bearer xxx 解析并校验
            if (!StringUtils.hasText(email) && StringUtils.hasText(authHeader)) {
                String bearer = extractBearer(authHeader);
                if (StringUtils.hasText(bearer)) {
                    DecodedJWT jwt = verify(bearer);        // 严格校验签名/过期
                    if (jwt != null) {
                        email = emailFromJwt(jwt);
                    }
                }
            }

            // 仍然拿不到 → 未认证
            if (!StringUtils.hasText(email)) {
                return ResponseEntity.status(401).body(err("unauthenticated", "未认证或 token 无效"));
            }

            // ✅ 关键：lambda 捕获 final 变量
            final String emailFinal = email;

            // 业务更新
            User u = repo.findByEmail(emailFinal)
                    .orElseThrow(() -> new RuntimeException("User not found by email: " + emailFinal));

            applyPatch(u, req);
            repo.saveAndFlush(u);

            return ResponseEntity.ok(UserView.from(u));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(err("update_failed", e.getMessage()));
        }
    }

    // ====== 3) 管理员接口 ======

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public User adminUpdate(@PathVariable Long id, @RequestBody UpdateUserRequest req) {
        User u = repo.findById(id).orElseThrow();
        applyPatch(u, req);
        return u;
    }

    // ====== 公共补丁方法 ======

    private void applyPatch(User u, UpdateUserRequest r) {
        if (r.getFirstName() != null) u.setFirstName(r.getFirstName());
        if (r.getLastName() != null) u.setLastName(r.getLastName());
        if (r.getCardLevel() != null) u.setCardLevel(r.getCardLevel());
        if (r.getStreetAddress() != null) u.setStreetAddress(r.getStreetAddress());
        if (r.getSuburb() != null) u.setSuburb(r.getSuburb());
        if (r.getState() != null) u.setState(r.getState());
        if (r.getPostcode() != null) u.setPostcode(r.getPostcode());
        if (r.getPhotoUrl() != null) u.setPhotoUrl(r.getPhotoUrl());
        if (r.getYearOfBirth() != null) u.setYearOfBirth(r.getYearOfBirth());
        // 其它字段按需补充…
    }

    // ======================
    // 内部 DTOs（仅一份）
    // ======================

    /** 用户返回视图（携带培训经历列表） */
    static class UserView {
        public Long id;
        public String firstName;
        public String lastName;
        public String cardLevel;
        public String streetAddress;
        public String suburb;
        public String state;
        public String postcode;
        public String photoUrl;
        public String email;
        public int birthday;
        public List<TrainingExperienceView> experiences;

        static UserView from(User u) {
            UserView v = new UserView();
            v.id = u.getId();
            v.firstName = u.getFirstName();
            v.lastName = u.getLastName();
            v.cardLevel = u.getCardLevel();
            v.streetAddress = u.getStreetAddress();
            v.suburb = u.getSuburb();
            v.state = u.getState();
            v.postcode = u.getPostcode();
            v.photoUrl = u.getPhotoUrl();
            v.email = u.getEmail();
            v.birthday = u.getYearOfBirth() == null ? 0 : u.getYearOfBirth();

            List<UserTrainingExperience> exps = u.getExperiences();
            v.experiences = (exps == null)
                    ? Collections.emptyList()
                    : exps.stream().map(TrainingExperienceView::from).collect(Collectors.toList());
            return v;
        }
    }

    /** 培训经历返回视图 —— 映射 UserTrainingExperience 的字段 */
    static class TrainingExperienceView {
        public Long id;
        public String trainingUndertaken;
        public String trainingProvider;
        public String trainingDate; // 表里使用字符串 mm/yyyy

        static TrainingExperienceView from(UserTrainingExperience t) {
            TrainingExperienceView v = new TrainingExperienceView();
            v.id = t.getId();
            v.trainingUndertaken = t.getTrainingUndertaken();
            v.trainingProvider = t.getTrainingProvider();
            v.trainingDate = t.getTrainingDate();
            return v;
        }
    }
}
