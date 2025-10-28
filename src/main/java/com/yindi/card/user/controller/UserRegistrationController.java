package com.yindi.card.user.controller;

import com.yindi.card.auth.EmailVerifyService;
import com.yindi.card.auth.dto.VerifyResult;
import com.yindi.card.user.CardIssueStatus;
import com.yindi.card.user.User;
import com.yindi.card.user.UserRepository;
// import com.yindi.card.user.UserTrainingExperience; // 暂未使用
import com.yindi.card.user.dto.UserRegistrationRequest;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserRegistrationController {

    private final UserRepository userRepository;
    private final EmailVerifyService emailVerifyService;

    public UserRegistrationController(UserRepository userRepository,
                                      EmailVerifyService emailVerifyService) {
        this.userRepository = userRepository;
        this.emailVerifyService = emailVerifyService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationRequest request) {
        // 1) 校验验证码
        VerifyResult vr = emailVerifyService.verify(request.getEmail(), request.getVerifyCode());
        if (!vr.verified()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "registered", false,
                    "error", "INVALID_CODE",
                    "message", "Invalid or expired verification code"
            ));
        }

        // 2) 邮箱是否已注册
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(409).body(Map.of(
                    "registered", false,
                    "error", "EMAIL_IN_USE",
                    "message", "Email already registered"
            ));
        }

        // 3) 构建 User
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setYearOfBirth(request.getYearOfBirth());
        user.setCardLevel(request.getCardLevel());
        user.setStreetAddress(request.getStreetAddress());
        user.setSuburb(request.getSuburb());
        user.setState(request.getState());
        user.setPostcode(request.getPostcode());
        user.setEmail(request.getEmail());
        user.setPhotoUrl(request.getPhotoUrl());

        // 3.1 必要默认值（避免写 NULL 触发 NOT NULL 约束）
        if (user.getStatus() == null || user.getStatus().isBlank()) {
            user.setStatus("PENDING");
        }
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }
        if (user.getIsActive() == null) {
            user.setIsActive(false);
        }
        if (user.getCardIssueStatus() == null) {
            user.setCardIssueStatus(CardIssueStatus.REQUESTED); // 与实体枚举一致
        }

        // 4) 训练经历（你先注释了就不处理）
        // if (request.getExperiences() != null) { ... }

        // 5) 保存（捕获数据库层异常，避免 500)
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            // 常见原因：列为 NOT NULL、长度超限、唯一约束冲突等
            String msg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("registered", false);
            body.put("error", "DATA_INTEGRITY");
            body.put("message", msg);
            return ResponseEntity.badRequest().body(body);
        }

        return ResponseEntity.ok(Map.of(
                "registered", true,
                "userId", user.getId(),
                "token", vr.token()
        ));
    }
}
