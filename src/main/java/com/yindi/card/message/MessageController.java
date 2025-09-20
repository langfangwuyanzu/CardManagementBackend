package com.yindi.card.message;

import com.yindi.card.message.dto.CreateQuestionRequest;
import com.yindi.card.message.dto.CreateReplyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /** 新建主帖（发问题） */
    @PostMapping("/questions")
    public ResponseEntity<Message> createQuestion(@RequestBody CreateQuestionRequest req) {
        Message created = messageService.createQuestion(req);
        return ResponseEntity.ok(created);
    }

    /** 在线程下回复（用户或管理员） */
    @PostMapping("/{threadId}/replies")
    public ResponseEntity<Message> createReply(@PathVariable Long threadId,
                                               @RequestBody CreateReplyRequest req) {
        Message created = messageService.createReply(threadId, req);
        return ResponseEntity.ok(created);
    }

    /** 获取一个线程内的所有消息（正序） */
    @GetMapping("/threads/{threadId}")
    public ResponseEntity<List<Message>> getThread(@PathVariable Long threadId) {
        List<Message> list = messageService.getThread(threadId);
        return ResponseEntity.ok(list);
    }

    /** 用户自己的未发送给管理员的主帖（按创建时间倒序） */
    @GetMapping("/users/{userId}/unsent")
    public ResponseEntity<Page<Message>> listUserUnsent(@PathVariable Long userId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        Page<Message> p = messageService.listUserUnsent(userId, page, size);
        return ResponseEntity.ok(p);
    }

    /** 批量标记（或取消）该用户的主帖为“发给管理员” */
    @PutMapping("/users/{userId}/sent-to-admin")
    public ResponseEntity<Void> markSentToAdmin(@PathVariable Long userId,
                                                @RequestParam(name = "value") boolean sentToAdmin) {
        messageService.markSentToAdmin(userId, sentToAdmin);
        return ResponseEntity.ok().build();
    }

    /** 管理员待办：发给管理员且尚无管理员回复的主帖 */
    @GetMapping("/admin/todo")
    public ResponseEntity<Page<Message>> adminTodo(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        Page<Message> p = messageService.adminTodo(page, size);
        return ResponseEntity.ok(p);
    }

    /** 管理员历史：已有管理员回复的主帖 */
    @GetMapping("/admin/history")
    public ResponseEntity<Page<Message>> adminHistory(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        Page<Message> p = messageService.adminHistory(page, size);
        return ResponseEntity.ok(p);
    }

    /** 隐藏/删除单条消息（此处做硬删除；需要软删除可自行扩展字段） */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> hideMessage(@PathVariable Long id) {
        messageService.hideMessage(id);
        return ResponseEntity.noContent().build();
    }
}
