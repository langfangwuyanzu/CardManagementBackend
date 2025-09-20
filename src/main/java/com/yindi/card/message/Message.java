package com.yindi.card.message;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息实体（支持自引用线程）
 *
 * 表结构建议：
 *  - messages(
 *      id BIGINT PK AUTO_INCREMENT,
 *      thread_id BIGINT,            -- 线程根消息ID（主帖的 thread_id = 自己的 id）
 *      parent_id BIGINT,            -- 直接父消息ID（主帖为 NULL）
 *      author_user_id BIGINT,       -- 作者用户ID（不做外键校验）
 *      content TEXT,
 *      message_type VARCHAR(32),
 *      author_role VARCHAR(32),
 *      sent_to_admin BOOLEAN,
 *      has_admin_reply BOOLEAN,
 *      last_admin_reply_at DATETIME,
 *      created_at DATETIME NOT NULL,
 *      updated_at DATETIME NOT NULL,
 *      CONSTRAINT fk_messages_thread FOREIGN KEY (thread_id) REFERENCES messages(id) ON DELETE CASCADE
 *    )
 */
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 线程根消息ID：主帖保存后再把 threadId 回写成自身 id */
    @Column(name = "thread_id")
    private Long threadId;

    /** 直接父消息ID：主帖为 null，回复为父消息的 id */
    @Column(name = "parent_id")
    private Long parentId;

    /** 作者用户ID（按你要求，不校验是否存在） */
    @Column(name = "author_user_id", nullable = false)
    private Long authorUserId;

    /** 文本内容 */
    @Lob
    @Column(name = "content")
    private String content;

    /** 消息类型：与你的枚举 MessageType 对齐（例如 TEXT/IMAGE/FILE） */
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", length = 32, nullable = false)
    private MessageType messageType;

    /** 作者角色：USER / ADMIN */
    @Enumerated(EnumType.STRING)
    @Column(name = "author_role", length = 32, nullable = false)
    private AuthorRole authorRole;

    /** 是否作为“发给管理员”的主贴 */
    @Column(name = "sent_to_admin", nullable = false)
    private boolean sentToAdmin = false;

    /** 该线程是否已经有管理员回复（标记在主帖上） */
    @Column(name = "has_admin_reply", nullable = false)
    private boolean hasAdminReply = false;

    /** 最后一次管理员回复时间（标记在主帖上） */
    @Column(name = "last_admin_reply_at")
    private LocalDateTime lastAdminReplyAt;

    /** 创建/更新时间 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /* -------------------- 生命周期钩子：自动维护时间戳 -------------------- */

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
        // 如果没指定类型，给个默认 TEXT（可按需修改）
        if (messageType == null) messageType = MessageType.TEXT;
        if (authorRole == null) authorRole = AuthorRole.USER;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /* -------------------- Getter / Setter -------------------- */

    public Long getId() {
        return id;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getAuthorUserId() {
        return authorUserId;
    }

    public void setAuthorUserId(Long authorUserId) {
        this.authorUserId = authorUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    /** 兼容 Service 的 setMessageType 调用 */
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public AuthorRole getAuthorRole() {
        return authorRole;
    }

    public void setAuthorRole(AuthorRole authorRole) {
        this.authorRole = authorRole;
    }

    public boolean isSentToAdmin() {
        return sentToAdmin;
    }

    public void setSentToAdmin(boolean sentToAdmin) {
        this.sentToAdmin = sentToAdmin;
    }

    /** 同时提供 is/get 两种形式以兼容不同调用 */
    public boolean isHasAdminReply() {
        return hasAdminReply;
    }

    public boolean getHasAdminReply() {
        return hasAdminReply;
    }

    public void setHasAdminReply(boolean hasAdminReply) {
        this.hasAdminReply = hasAdminReply;
    }

    public LocalDateTime getLastAdminReplyAt() {
        return lastAdminReplyAt;
    }

    /** 直接赋值，不再调用不存在的方法 */
    public void setLastAdminReplyAt(LocalDateTime lastAdminReplyAt) {
        this.lastAdminReplyAt = lastAdminReplyAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Column(name = "hidden", nullable = false)
    private boolean hidden = false;

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

}
