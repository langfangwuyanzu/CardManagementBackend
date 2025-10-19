package com.yindi.card.message;

import com.yindi.card.user.User;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class NotificationTemplateService {
    public String cardExpiryTitle(User u, LocalDate expireDate) {
        return "Card Expiry Reminder — Your Yolŋu Card will expire on " + expireDate;
    }

    public String cardExpiryContent(User u, LocalDate expireDate, int daysLeft) {
        return """
           %s %s,
           
           Your Yolŋu Card will expire on %s (in %d day%s).
           Please renew in time to avoid any service disruption.
           If you have already renewed, you can ignore this message.
           """.formatted(
                Optional.ofNullable(u.getFirstName()).orElse(""),
                Optional.ofNullable(u.getLastName()).orElse(""),
                expireDate, daysLeft, daysLeft == 1 ? "" : "s"
        );
    }

}
