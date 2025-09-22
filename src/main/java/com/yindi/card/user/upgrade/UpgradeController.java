package com.yindi.card.user.upgrade;

import com.yindi.card.user.User;
import com.yindi.card.user.UserRepository;
import com.yindi.card.user.UserTrainingExperience;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/upgrade")
public class UpgradeController {

    private final UserRepository userRepo;

    public UpgradeController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping("/{userId}")
    @Transactional
    public ResponseEntity<?> upgradeCard(
            @PathVariable Long userId,
            @Valid @RequestBody UpgradeRequest req) {

        // Search user
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // update card level
        user.setCardLevel(req.getNewLevel());

        // Additional training experience
        UserTrainingExperience exp = new UserTrainingExperience();
        exp.setTrainingUndertaken(req.getTrainingUndertaken());
        exp.setTrainingProvider(req.getTrainingProvider());
        exp.setTrainingDate(req.getTrainingDate());
        exp.setUser(user);

        user.getExperiences().add(exp);

        userRepo.save(user);

        return ResponseEntity.ok("Upgrade request submitted successfully.");
    }
}
