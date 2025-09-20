// src/main/java/com/yindi/card/db/DbPingController.java
package com.yindi.card.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DbPingController {
    private final JdbcTemplate jdbc;

    public DbPingController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/api/db/ping")
    public ResponseEntity<?> ping() {
        try {
            String now = jdbc.queryForObject("select now()", String.class);
            return ResponseEntity.ok(Map.of("ok", true, "dbNow", now));
        } catch (Exception e) {
            // 把具体错误返回，便于定位
            return ResponseEntity.status(500).body(Map.of(
                    "ok", false,
                    "error", e.getClass().getName(),
                    "message", e.getMessage()
            ));
        }
    }
}
