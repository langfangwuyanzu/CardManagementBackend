package com.yindi.card.user.upgrade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yindi.card.user.User;
import com.yindi.card.user.UserRepository;
import com.yindi.card.user.UserTrainingExperience;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/upgrade")
public class UpgradeController {

    private final UserRepository userRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UpgradeController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // A. 前端用：JSON part + file
    @PostMapping(value="/upload/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> uploadJsonPart(
            @PathVariable Long userId,
            @RequestPart("request") String requestJson,
            @RequestPart(value="file", required=false) MultipartFile file
    ) throws Exception {
        UpgradeRequest req = objectMapper.readValue(requestJson, UpgradeRequest.class);
        save(userId, req, file);  // 复用保存逻辑
        return ResponseEntity.ok("OK(JSON part)");
    }

    // B. Swagger/手动测试用：表单字段 + file（不需要 JSON part）
    @PostMapping(value="/upload-form/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> uploadFormFields(
            @PathVariable Long userId,
            @RequestParam String newLevel,
            @RequestParam String trainingUndertaken,
            @RequestParam String trainingProvider,
            @RequestParam String trainingDate,
            @RequestPart(value="file", required=false) MultipartFile file
    ) {
        UpgradeRequest req = new UpgradeRequest();
        req.setNewLevel(newLevel);
        req.setTrainingUndertaken(trainingUndertaken);
        req.setTrainingProvider(trainingProvider);
        req.setTrainingDate(trainingDate);
        save(userId, req, file);
        return ResponseEntity.ok("OK(form)");
    }

    private void save(Long userId, UpgradeRequest req, MultipartFile file) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setCardLevel(req.getNewLevel());

        UserTrainingExperience exp = new UserTrainingExperience();
        exp.setTrainingUndertaken(req.getTrainingUndertaken());
        exp.setTrainingProvider(req.getTrainingProvider());
        exp.setTrainingDate(req.getTrainingDate());
        exp.setUser(user);
        user.getExperiences().add(exp);

        // TODO: 如需保存文件，file 在这里处理
        userRepo.save(user);
    }
}
