package com.yindi.card.message;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReminderScheduler {
    private final CardExpiryReminderService service;
    public ReminderScheduler(CardExpiryReminderService service){ this.service = service; }

    @Scheduled(cron = "0 0 9 * * *", zone = "Australia/Adelaide")
    public void daily(){ service.runOnce(); }
}
