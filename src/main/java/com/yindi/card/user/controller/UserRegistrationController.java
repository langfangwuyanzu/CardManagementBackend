package com.yindi.card.user.controller;

import com.yindi.card.auth.EmailVerifyService;
import com.yindi.card.auth.dto.VerifyResult;
import com.yindi.card.user.User;
import com.yindi.card.user.UserRepository;
import com.yindi.card.user.UserTrainingExperience;
import com.yindi.card.user.dto.UserRegistrationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
        // 1) 校验验证码
        VerifyResult vr = emailVerifyService.verify(request.getEmail(), request.getVerifyCode());
        if (!vr.verified()) {
            return ResponseEntity.ok(Map.of(
                    "registered", false,
                    "message", "Invalid or expired verification code"
            ));
        }

        // 2) 邮箱是否已注册
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.ok(Map.of(
                    "registered", false,
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

        // 4) 训练经历
//        if (request.getExperiences() != null) {
//            for (UserRegistrationRequest.ExperienceDto dto : request.getExperiences()) {
//                UserTrainingExperience exp = new UserTrainingExperience();
//                exp.setTrainingName(dto.getTrainingName());
//                exp.setTrainingProvider(dto.getTrainingProvider());
//                exp.setDateOfTraining(dto.getDateOfTraining());
//                user.addExperience(exp); // 会自动设置 exp.setUser(user)
//            }
//        }

        // 5) 保存
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "registered", true,
                "userId", user.getId(),
                "token", vr.token()
        ));
    }
}
