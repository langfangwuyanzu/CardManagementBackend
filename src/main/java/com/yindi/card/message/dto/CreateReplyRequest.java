package com.yindi.card.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yindi.card.message.AuthorRole;
import com.yindi.card.message.MessageType;

public class CreateReplyRequest {

    @JsonProperty("parentId")
    private Long parentId;

    @JsonProperty("authorUserId")
    private Long authorUserId;

    @JsonProperty("authorRole")
    private AuthorRole authorRole;     // USER / ADMIN

    @JsonProperty("content")
    private String content;

    @JsonProperty("type")
    private MessageType type;

    // ==== 必须提供给 Service 的 getter ====
    public Long getParentId() { return parentId; }
    public Long getAuthorUserId() { return authorUserId; }
    public AuthorRole getAuthorRole() { return authorRole; }
    public String getContent() { return content; }
    public MessageType getType() { return type; }

    // ==== setter（可选） ====
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public void setAuthorUserId(Long authorUserId) { this.authorUserId = authorUserId; }
    public void setAuthorRole(AuthorRole authorRole) { this.authorRole = authorRole; }
    public void setContent(String content) { this.content = content; }
    public void setType(MessageType type) { this.type = type; }

    // 兼容旧字段名（如果你之前用的是 messageType/role 等）
    @JsonProperty("messageType")
    public void setMessageTypeAlias(MessageType t) { this.type = t; }

    @JsonProperty("role")
    public void setRoleAlias(AuthorRole r) { this.authorRole = r; }
}
