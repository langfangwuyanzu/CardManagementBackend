package com.yindi.card.message;

import com.yindi.card.message.dto.CreateQuestionRequest;
import com.yindi.card.message.dto.CreateReplyRequest;
import com.yindi.card.message.repo.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * 用户创建消息。
     * 不校验用户是否存在，只记录 authorUserId。
     * 若 parentId 为空 => 新建主帖；先保存拿到 id，再把 threadId 设置为自身 id。
     */
    @Transactional
    public Message createUserMessage(Long authorUserId,
                                     String content,
                                     MessageType type,
                                     Long parentId,
                                     boolean sendToAdmin) {

        if (authorUserId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "authorUserId required");
        }
        if (type == null) type = MessageType.TEXT;

        LocalDateTime now = LocalDateTime.now();

        Message msg = new Message();
        msg.setAuthorUserId(authorUserId);
        msg.setAuthorRole(AuthorRole.USER);
        msg.setMessageType(type);
        msg.setContent(content);
        msg.setParentId(parentId);
        msg.setSentToAdmin(sendToAdmin);
        msg.setHasAdminReply(false);
        msg.setCreatedAt(now);
        msg.setUpdatedAt(now);

        // 先保存，得到主键 id
        Message saved = messageRepository.save(msg);

        // 若是新主帖 => 将 threadId 回写成自身 id
        if (parentId == null) {
            saved.setThreadId(saved.getId());
            saved = messageRepository.save(saved);
        }

        return saved;
    }

    /**
     * 回复消息（用户或管理员）。
     * 需要明确传入 threadId 与 parentId。
     * 如果 authorRole = ADMIN，则会把主贴标记为“已有管理员回复”，并更新 lastAdminReplyAt。
     */
    @Transactional
    public Message reply(Long threadId,
                         Long parentId,
                         Long authorUserId,
                         AuthorRole authorRole,
                         String content,
                         MessageType type) {

        if (threadId == null || parentId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "threadId and parentId required");
        }
        if (authorRole == null) authorRole = AuthorRole.USER;
        if (type == null) type = MessageType.TEXT;

        LocalDateTime now = LocalDateTime.now();

        Message reply = new Message();
        reply.setThreadId(threadId);
        reply.setParentId(parentId);
        reply.setAuthorUserId(authorUserId);
        reply.setAuthorRole(authorRole);
        reply.setMessageType(type);
        reply.setContent(content);
        reply.setSentToAdmin(false);
        reply.setCreatedAt(now);
        reply.setUpdatedAt(now);

        Message saved = messageRepository.save(reply);

        if (authorRole == AuthorRole.ADMIN) {
            markThreadHasAdminReply(threadId, now);
        }

        return saved;
    }

    /**
     * 用户查看自己的主帖（不是发给管理员的），按创建时间倒序。
     */
    @Transactional(readOnly = true)
    public Page<Message> listUserTopThreads(Long userId, Pageable pageable) {
        return messageRepository
                .findByAuthorUserIdAndParentIdIsNullAndSentToAdminFalseOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * 管理员收件箱：新的（尚无管理员回复）的主帖。
     */
    @Transactional(readOnly = true)
    public Page<Message> listAdminInboxNew(Pageable pageable) {
        return messageRepository
                .findByParentIdIsNullAndSentToAdminTrueAndHasAdminReplyFalseOrderByCreatedAtDesc(pageable);
    }

    /**
     * 管理员收件箱：已回复过的主帖（按最后管理员回复时间排序）。
     */
    @Transactional(readOnly = true)
    public Page<Message> listAdminInboxReplied(Pageable pageable) {
        return messageRepository
                .findByParentIdIsNullAndSentToAdminTrueAndHasAdminReplyTrueOrderByLastAdminReplyAtDesc(pageable);
    }

    /**
     * 拉取一个线程内的所有消息（正序）。
     */
    @Transactional(readOnly = true)
    public List<Message> getThreadMessages(Long threadId) {
        return messageRepository.findByThreadIdOrderByCreatedAtAsc(threadId);
    }

    // ---------- private helpers ----------

    private void markThreadHasAdminReply(Long threadId, LocalDateTime when) {
        List<Message> messages = messageRepository.findByThreadIdOrderByCreatedAtAsc(threadId);
        if (messages.isEmpty()) return;

        Message root = messages.get(0);
        if (root.getParentId() != null && !Objects.equals(root.getId(), root.getThreadId())) {
            return;
        }

        if (!root.getHasAdminReply()) {
            root.setHasAdminReply(true);
        }
        root.setLastAdminReplyAt(when);
        root.setUpdatedAt(when);
        messageRepository.save(root);
    }

    // ========= 适配 Controller 所需的签名 =========

    @Transactional
    public Message createQuestion(CreateQuestionRequest req) {
        MessageType type = (req.getType() != null) ? req.getType() : MessageType.TEXT;
        return createUserMessage(
                req.getAuthorUserId(),
                req.getContent(),
                type,
                null,
                Boolean.TRUE.equals(req.getSendToAdmin())
        );
    }

    @Transactional
    public Message createReply(Long threadId, CreateReplyRequest req) {
        MessageType type = (req.getType() != null) ? req.getType() : MessageType.TEXT;
        AuthorRole role = (req.getAuthorRole() != null) ? req.getAuthorRole() : AuthorRole.USER;
        return reply(
                threadId,
                req.getParentId(),
                req.getAuthorUserId(),
                role,
                req.getContent(),
                type
        );
    }

    @Transactional(readOnly = true)
    public List<Message> getThread(Long threadId) {
        return getThreadMessages(threadId);
    }

    @Transactional(readOnly = true)
    public Page<Message> listUserUnsent(Long userId, int page, int size) {
        return listUserTopThreads(userId, PageRequest.of(page, size));
    }

    @Transactional
    public void markSentToAdmin(Long userId, boolean sentToAdmin) {
        Page<Message> p = listUserTopThreads(userId, PageRequest.of(0, 200));
        List<Message> list = p.getContent();
        for (Message m : list) {
            m.setSentToAdmin(sentToAdmin);
        }
        messageRepository.saveAll(list);
    }

    @Transactional(readOnly = true)
    public Page<Message> adminTodo(int page, int size) {
        return messageRepository
                .findByParentIdIsNullAndSentToAdminTrueAndHasAdminReplyFalseOrderByCreatedAtDesc(
                        PageRequest.of(page, size)
                );
    }

    @Transactional(readOnly = true)
    public Page<Message> adminHistory(int page, int size) {
        return messageRepository
                .findByParentIdIsNullAndSentToAdminTrueAndHasAdminReplyTrueOrderByLastAdminReplyAtDesc(
                        PageRequest.of(page, size)
                );
    }

    @Transactional
    public void hideMessage(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
        messageRepository.deleteById(id);
    }
}
