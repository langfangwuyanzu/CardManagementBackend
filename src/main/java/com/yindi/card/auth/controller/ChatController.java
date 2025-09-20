//package com.yindi.card.auth.controller;
//
//import com.yindi.card.chat.ChatMessage;
//import org.springframework.messaging.handler.annotation.DestinationVariable;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//@Controller
//public class ChatController {
//
//    private final SimpMessagingTemplate messagingTemplate;
//    // 约定：userId=1 是管理员
//    private static final long ADMIN_ID = 1L;
//
//    public ChatController(SimpMessagingTemplate messagingTemplate) {
//        this.messagingTemplate = messagingTemplate;
//    }
//
//    /**
//     * 普通用户向自己的会话频道发消息。
//     * 前端发送到：/app/user/{userId}
//     * 服务器转发到：/topic/user/{userId}
//     * 并抄送管理员总线：/topic/admin
//     */
//    @MessageMapping("/user/{userId}")
//    public void userSend(@DestinationVariable Long userId, ChatMessage msg) {
//        msg.setTimestamp(System.currentTimeMillis());
//        // 发到用户专属频道（让对话双方都能看到：用户自己 & 管理员订阅该用户频道时也能看到）
//        messagingTemplate.convertAndSend("/topic/user/" + userId, msg);
//        // 抄送管理员总线，驱动管理员“会话列表”
//        messagingTemplate.convertAndSend("/topic/admin", msg);
//    }
//
//    /**
//     * 管理员给指定用户发送消息。
//     * 前端（管理员）发送到：/app/admin/send/{targetUserId}
//     * 服务器转发到：/topic/user/{targetUserId}
//     */
//    @MessageMapping("/admin/send/{targetUserId}")
//    public void adminSend(@DestinationVariable Long targetUserId, ChatMessage msg) {
//        msg.setTimestamp(System.currentTimeMillis());
//        msg.setFromId(ADMIN_ID); // 标记来自管理员
//        messagingTemplate.convertAndSend("/topic/user/" + targetUserId, msg);
//        // 也可以同步抄送到 admin 总线，做消息归档/列表更新
//        messagingTemplate.convertAndSend("/topic/admin", msg);
//    }
//}
