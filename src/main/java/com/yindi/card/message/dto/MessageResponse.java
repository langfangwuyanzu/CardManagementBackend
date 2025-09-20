// com.yindi.card.message.dto.MessageResponse.java
package com.yindi.card.message.dto;

import com.yindi.card.message.*;
import java.time.LocalDateTime;

public record MessageResponse(
        Long id, Long threadId, Long parentId,
        Long authorUserId, AuthorRole authorRole,
        MessageType type, String content,
        boolean sentToAdmin, boolean hasAdminReply, boolean hidden,
        LocalDateTime createdAt, LocalDateTime updatedAt
) {
    public static MessageResponse of(Message m) {
        return new MessageResponse(
                m.getId(),
                m.getThreadId(),
                m.getParentId(),
                m.getAuthorUserId(),
                m.getAuthorRole(),
                m.getMessageType(),     // ✅ 改这里
                m.getContent(),
                m.isSentToAdmin(),
                m.isHasAdminReply(),
                m.isHidden(),           // ✅ 前提是实体里有 hidden 字段
                m.getCreatedAt(),
                m.getUpdatedAt()
        );
    }

}
