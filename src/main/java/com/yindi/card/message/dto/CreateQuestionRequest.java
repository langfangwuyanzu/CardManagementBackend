package com.yindi.card.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yindi.card.message.MessageType;

public class CreateQuestionRequest {

    // 这些字段名用于接收 JSON；@JsonProperty 让不同命名也能映射进来
    @JsonProperty("authorUserId")
    private Long authorUserId;

    @JsonProperty("content")
    private String content;

    @JsonProperty("sendToAdmin")
    private Boolean sendToAdmin;

    @JsonProperty("type")              // 前端传 "type": "TEXT"
    private MessageType type;

    // ==== 必须提供给 Service 的 getter ====
    public Long getAuthorUserId() { return authorUserId; }
    public String getContent() { return content; }
    public Boolean getSendToAdmin() { return sendToAdmin; }
    public MessageType getType() { return type; }

    // ==== 可选：setter（便于测试或别处复用） ====
    public void setAuthorUserId(Long authorUserId) { this.authorUserId = authorUserId; }
    public void setContent(String content) { this.content = content; }
    public void setSendToAdmin(Boolean sendToAdmin) { this.sendToAdmin = sendToAdmin; }
    public void setType(MessageType type) { this.type = type; }

    // ==== 若你原来字段叫 messageType，可加一个别名 setter 做兼容 ====
    @JsonProperty("messageType")
    public void setMessageTypeAlias(MessageType t) { this.type = t; }
}
