package com.yindi.card.user.renewal;

import com.yindi.card.user.User;
import com.yindi.card.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/renewal")
public class RenewalController {

    private final UserRepository userRepo;

    public RenewalController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping("/{userId}")
    @Transactional
    public ResponseEntity<?> renewCard(@PathVariable Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 假设 User 有一个 validUntil 字段（LocalDate 类型）
        LocalDate newExpiry = LocalDate.now().plusYears(3);
        user.setExpireDate(newExpiry);

        userRepo.save(user);

        return ResponseEntity.ok("Card renewed until " + newExpiry);
    }
}
