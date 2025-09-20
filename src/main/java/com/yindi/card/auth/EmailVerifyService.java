package com.yindi.card.auth;

import com.yindi.card.auth.dto.VerifyResult;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class EmailVerifyService {

    private final StringRedisTemplate redis;
    private final JavaMailSender mailSender;
    private final SecureRandom random = new SecureRandom();

    private final String from;
    private final long codeTtlSec;
    private final long cooldownSec;
    private final byte[] jwtKeyBytes;
    private final long jwtTtlSec;

    public EmailVerifyService(StringRedisTemplate redis,
                              JavaMailSender mailSender,
                              @Value("${app.mail.from}") String from,
                              @Value("${app.verify.code-ttl-seconds}") long codeTtlSec,
                              @Value("${app.verify.send-cooldown-seconds}") long cooldownSec,
                              @Value("${app.verify.jwt-secret}") String jwtSecret,
                              @Value("${app.verify.jwt-ttl-seconds}") long jwtTtlSec) {
        this.redis = redis;
        this.mailSender = mailSender;
        this.from = from;
        this.codeTtlSec = codeTtlSec;
        this.cooldownSec = cooldownSec;
        this.jwtKeyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        this.jwtTtlSec = jwtTtlSec;
    }

    private String codeKey(String email) { return "email:verify:code:" + email.toLowerCase(); }
    private String cdKey(String email)   { return "email:verify:cd:" + email.toLowerCase(); }

    public void sendCode(String email) {
        if (Boolean.TRUE.equals(redis.hasKey(cdKey(email)))) {
            throw new RuntimeException("Please wait before requesting another code.");
        }

        String code = String.format("%06d", random.nextInt(1_000_000));

        redis.opsForValue().set(codeKey(email), code, codeTtlSec, java.util.concurrent.TimeUnit.SECONDS);
        redis.opsForValue().set(cdKey(email), "1", cooldownSec, java.util.concurrent.TimeUnit.SECONDS);

        sendEmail(email, code);
    }

    private void sendEmail(String to, String code) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Your verification code");
            String html =
                    "<div style='font-family:Inter,Arial,sans-serif;font-size:14px;color:#222'>" +
                            "<p>Hello,</p>" +
                            "<p>Your verification code is:</p>" +
                            "<p style='font-size:24px;font-weight:700;letter-spacing:2px'>" + code + "</p>" +
                            "<p>This code will expire in 5 minutes.</p>" +
                            "</div>";

            helper.setText(html, true);
            mailSender.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    public VerifyResult verify(String email, String code) {
        String real = redis.opsForValue().get(codeKey(email));
        boolean ok = real != null && real.equals(code);
        if (ok) {
            redis.delete(codeKey(email));
            String token = Jwts.builder()
                    .setSubject(email.toLowerCase())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtTtlSec * 1000))
                    .claim("scope", "email_verified")
                    .signWith(Keys.hmacShaKeyFor(jwtKeyBytes))
                    .compact();
            return new VerifyResult(true, token);
        }
        return new VerifyResult(false, null);
    }

    public boolean verifyToken(String token, String email) {
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtKeyBytes))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return email.equalsIgnoreCase(claims.getSubject())
                    && "email_verified".equals(claims.get("scope"));
        } catch (Exception e) {
            return false;
        }
    }
}
