package com.yindi.card.config;

import com.yindi.card.auth.JwtAuthFilter;
import com.yindi.card.auth.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // 直接在这里装配 JwtUtil / JwtAuthFilter，保持最小改动即可
    @Bean
    public JwtUtil jwtUtil(
            @Value("${app.jwt.secret:change_me_change_me_change_me_change_me_32B_min}") String secret,
            @Value("${app.jwt.ttlMillis:86400000}") long ttlMillis
    ) {
        return new JwtUtil(secret, ttlMillis);
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(JwtUtil jwtUtil) {
        return new JwtAuthFilter(jwtUtil);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 小项目通常是纯 API，关闭 CSRF
                .csrf(csrf -> csrf.disable())
                // 无状态会话
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 🔓 所有请求全部放行
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        // ❌ 不要再加自定义 JwtAuthFilter / 资源服务器等
        // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        // .oauth2ResourceServer(oauth2 -> oauth2.jwt());

        return http.build();
    }
//    SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
//        http.csrf(csrf -> csrf.disable())
//                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        // 仅放开真正的匿名接口
//                        .requestMatchers(
//                                "/api/auth/**",
//                                "/api/user/register",                 // 注意这是单数的注册接口
//                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
//                                "/actuator/**", "/error"
//                        ).permitAll()
//
//                        // /api/users/me 需要登录（已认证）
//                        .requestMatchers("/api/users/me").authenticated()
//                        // 其他 /api/users/** 也需要登录（看你需要可去掉）
//                        .requestMatchers("/api/users/**").authenticated()
//
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
