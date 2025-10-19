package com.yindi.card.message;

import com.yindi.card.user.User;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "notifications") // 如果数据源已经指向 yolngu_card 库，这里不需要 schema
// 如果你一定要写 schema: @Table(name = "notifications", schema = "yolngu_card")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== 关系 =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @com.fasterxml.jackson.annotation.JsonIgnore  // ← 关键：直接不序列化 user
    private User user;

    // ===== 业务字段 =====
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "scheduled_at", nullable = false)
    private OffsetDateTime scheduledAt = OffsetDateTime.now(ZoneId.of("Australia/Adelaide"));

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    @Lob
    private String error;

    @Column(name = "metadata", columnDefinition = "json")
    private String metadata;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now(ZoneId.of("Australia/Adelaide"));

    // ===== getters / setters =====

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public NotificationChannel getChannel() { return channel; }
    public void setChannel(NotificationChannel channel) { this.channel = channel; }

    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public OffsetDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(OffsetDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public OffsetDateTime getSentAt() { return sentAt; }
    public void setSentAt(OffsetDateTime sentAt) { this.sentAt = sentAt; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
