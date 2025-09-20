package com.yindi.card.auth.controller;

import com.yindi.card.auth.EmailVerifyService;
import com.yindi.card.auth.dto.SendCodeRequest;
import com.yindi.card.auth.dto.VerifyCodeRequest;
import com.yindi.card.auth.dto.VerifyResult;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Swagger/OpenAPI 注解
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth/email")
@Tag(name = "Email Verification", description = "Endpoints for sending and verifying email codes")
public class EmailVerifyController {

    private final EmailVerifyService service;

    public EmailVerifyController(EmailVerifyService service) {
        this.service = service;
    }

    @PostMapping("/send-code")
    @Operation(summary = "Send verification code", description = "Send a 6-digit code to the user email")
    public ResponseEntity<?> send(@Valid @RequestBody SendCodeRequest req) {
        service.sendCode(req.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify code", description = "Check the code and return a short-lived token if correct")
    public ResponseEntity<VerifyResult> verify(@Valid @RequestBody VerifyCodeRequest req) {
        return ResponseEntity.ok(service.verify(req.email(), req.code()));
    }
}
