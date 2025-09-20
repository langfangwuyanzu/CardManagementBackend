package com.yindi.card.auth.config;
// src/main/java/com/yindi/card/ws/WebSocketConfig.java


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 原生 WebSocket（不使用 SockJS）
        registry.addEndpoint("/ws")
                // 允许所有来源（开发期）
                .setAllowedOriginPatterns("*"); // 注意：是 *Patterns
        // 如果你仍然想用 SockJS，解开下面这行，并把前端 VITE_WS_URL 改 http://...
        // .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 客户端订阅前缀
        registry.enableSimpleBroker("/topic", "/queue");
        // 客户端发送到服务端的前缀（前端 SEND_DEST 要以 /app 开头）
        registry.setApplicationDestinationPrefixes("/app");
    }
}
