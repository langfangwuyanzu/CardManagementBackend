package com.yindi.card.message;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final CardExpiryReminderService expiryService;
    private final NotificationRepository repo;
    

    public NotificationController(CardExpiryReminderService expiryService, NotificationRepository repo) {
        this.expiryService = expiryService;
        this.repo = repo;
    }

    /** 手动触发到期扫描一次（便于联调） */
    @PostMapping("/jobs/card-expiry")
    public Map<String, Object> trigger() {
        int processed = expiryService.runOnce();
        return Map.of("processed", processed);
    }

    /** 列表（前端消息中心） */
    @GetMapping
    public Page<com.yindi.card.message.NotificationListItemDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) NotificationStatus status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<Notification> spec = (root, cq, cb) -> {
            List<jakarta.persistence.criteria.Predicate> ps = new ArrayList<>();
            if (type != null) ps.add(cb.equal(root.get("type"), type));
            if (status != null) ps.add(cb.equal(root.get("status"), status));
            return cb.and(ps.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
        return repo.findAll(spec, pageable).map(com.yindi.card.message.NotificationListItemDTO::fromEntity);
    }




}
