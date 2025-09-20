package com.yindi.card.message.repo;

import com.yindi.card.message.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // 用户自己的“主帖”（不是发给管理员的）
    Page<Message>
    findByAuthorUserIdAndParentIdIsNullAndSentToAdminFalseOrderByCreatedAtDesc(
            Long authorUserId, Pageable pageable
    );

    // 发给管理员的主帖，尚无管理员回复（按创建时间降序）
    Page<Message>
    findByParentIdIsNullAndSentToAdminTrueAndHasAdminReplyFalseOrderByCreatedAtDesc(
            Pageable pageable
    );

    // 发给管理员的主帖，已有管理员回复（按最后回复时间降序）
    Page<Message>
    findByParentIdIsNullAndSentToAdminTrueAndHasAdminReplyTrueOrderByLastAdminReplyAtDesc(
            Pageable pageable
    );

    // 读取一个线程内的所有消息（按时间正序）
    List<Message> findByThreadIdOrderByCreatedAtAsc(Long threadId);
}
