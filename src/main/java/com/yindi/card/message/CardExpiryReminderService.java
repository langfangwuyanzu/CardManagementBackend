package com.yindi.card.message;

import com.yindi.card.user.User;
import com.yindi.card.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.List;

@Service
public class CardExpiryReminderService {
    private final UserRepository userRepo;
    private final NotificationRepository notificationRepo;
    private final NotificationTemplateService template;
    private final NotificationSender sender;
    private final ZoneId tz = ZoneId.of("Australia/Adelaide");

    @Value("${app.reminder.expiry.daysAhead:30}")
    private int daysAhead;

    @Value("${app.reminder.expiry.dedupeWindowDays:15}")
    private int dedupeWindowDays;

    public CardExpiryReminderService(UserRepository userRepo,
                                     NotificationRepository notificationRepo,
                                     NotificationTemplateService template,
                                     NotificationSender sender) {
        this.userRepo = userRepo;
        this.notificationRepo = notificationRepo;
        this.template = template;
        this.sender = sender;
    }

    @Transactional
    public int runOnce() {
        LocalDate today = LocalDate.now(tz);
        LocalDate to = today.plusDays(daysAhead);
        List<User> users = userRepo.findExpiringBetween(today, to);

        int count = 0;
        OffsetDateTime now = OffsetDateTime.now(tz);
        OffsetDateTime dedupeStart = now.minusDays(dedupeWindowDays);
        OffsetDateTime dedupeEnd = now.plusDays(1);

        for (User u : users) {
            LocalDate exp = u.getExpireDate();
            if (exp == null) continue;

            // 去重：最近 dedupeWindowDays 内已发过就跳过
            if (notificationRepo.existsByUserIdAndTypeAndScheduledAtBetween(
                    u.getId(), NotificationType.CARD_EXPIRY, dedupeStart, dedupeEnd)) {
                continue;
            }

            int daysLeft = Period.between(today, exp).getDays();
            Notification n = new Notification();
            n.setUser(u);
            n.setType(NotificationType.CARD_EXPIRY);
            n.setChannel(NotificationChannel.EMAIL);
            n.setTitle(template.cardExpiryTitle(u, exp));
            n.setContent(template.cardExpiryContent(u, exp, daysLeft));

            notificationRepo.save(n);

            try {
                sender.sendEmail(n);
                n.setStatus(NotificationStatus.SENT);
                n.setSentAt(OffsetDateTime.now(tz));
            } catch (Exception e) {
                n.setStatus(NotificationStatus.FAILED);
                n.setError(e.getMessage());
            }
            notificationRepo.save(n);
            count++;
        }
        return count;
    }
}
