package com.yindi.card.message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationSender {
    private final JavaMailSender mailSender;
    @Value("${app.mail.from:no-reply@yolngu.example}")
    private String from;

    public NotificationSender(JavaMailSender mailSender){ this.mailSender = mailSender; }

    public void sendEmail(Notification n){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(n.getUser().getEmail());
        msg.setSubject(n.getTitle());
        msg.setText(n.getContent());
        mailSender.send(msg);
    }
}
