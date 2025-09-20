package com.yindi.card.chat;

public class ChatMessage {
    private Long fromId;      // 发送方用户ID
    private String fromName;  // 发送方显示名
    private Long toId;        // 目标用户ID（管理员回消息时使用）
    private String content;
    private Long timestamp;

    // getters/setters
    public Long getFromId() { return fromId; }
    public void setFromId(Long fromId) { this.fromId = fromId; }
    public String getFromName() { return fromName; }
    public void setFromName(String fromName) { this.fromName = fromName; }
    public Long getToId() { return toId; }
    public void setToId(Long toId) { this.toId = toId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}
