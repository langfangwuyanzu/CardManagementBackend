package com.yindi.card.message;

import java.time.OffsetDateTime;

public record NotificationListItemDTO(
        Long id,
        String name,
        String email,
        String level,
        String type,
        String status,
        OffsetDateTime createdAt,
        String avatarUrl
) {
    public static NotificationListItemDTO fromEntity(Notification n) {
        var u = n.getUser();
        String fullName = (u == null) ? null :
                ((u.getFirstName() == null ? "" : u.getFirstName()) + " " +
                        (u.getLastName() == null ? "" : u.getLastName())).trim();

        return new NotificationListItemDTO(
                n.getId(),
                (fullName == null || fullName.isBlank()) ? null : fullName,
                u != null ? u.getEmail() : null,
                u != null ? u.getCardLevel() : null,
                n.getType() != null ? n.getType().name() : null,
                n.getStatus() != null ? n.getStatus().name() : null,
                n.getCreatedAt(),
                u != null ? u.getPhotoUrl() : null
        );
    }
}
