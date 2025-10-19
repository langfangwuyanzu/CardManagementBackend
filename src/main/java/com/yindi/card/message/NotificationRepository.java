package com.yindi.card.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

/**
 * Repository 接口：通知表
 *
 * 继承 JpaRepository（基础 CRUD）
 *      JpaSpecificationExecutor（支持动态条件查询 Specification）
 */
public interface NotificationRepository
        extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    /**
     * 判断某用户在指定时间窗口内是否已有某类型的通知
     * （Spring Data JPA 方法名推导生成 SQL）
     */
    boolean existsByUserIdAndTypeAndScheduledAtBetween(
            Long userId,
            NotificationType type,
            OffsetDateTime start,
            OffsetDateTime end
    );

    /**
     * 用 JPQL 写的自定义查询，功能与上面类似
     */
    @Query("select count(n) > 0 from Notification n " +
            "where n.user.id = :userId and n.type = :type " +
            "and n.scheduledAt between :start and :end")
    boolean existsWindow(@Param("userId") Long userId,
                         @Param("type") NotificationType type,
                         @Param("start") OffsetDateTime start,
                         @Param("end") OffsetDateTime end);
}
