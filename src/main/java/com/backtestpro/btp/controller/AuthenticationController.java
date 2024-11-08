package com.backtestpro.btp.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.backtestpro.btp.dto.AuthRequest;
import com.backtestpro.btp.service.AuthService;

@RestController
@RequestMapping("${app.api-prefix}/token")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest authRequest) {
        try {
            // 認證並生成 JWT token
            String token = authService.authenticateAndGenerateJwt(authRequest.getUsername(), authRequest.getPassword());

            // 構造回應的資料內容，將 token 放入 "data" 鍵中
            Map<String, String> responseData = new HashMap<>();
            responseData.put("token", token);

            // 返回包含 token 的 response body，並回傳 200 OK
            return ResponseEntity.ok(responseData);
        } catch (AuthenticationException e) {
            // 捕獲認證異常，返回 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Invalid credentials"));
        }
    }

    @PostMapping("/verify-token")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestHeader("Authorization") String authorizationHeader) {
        // 檢查 Authorization header 是否存在且以 "Bearer " 開頭
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            Map<String, Object> response = new HashMap<>();
            response.put("verify", false);
            response.put("message", "Authorization header is missing or incorrect");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // 提取 token
        String token = authorizationHeader.substring(7);

        // 驗證 token 是否有效
        if (authService.validateToken(token)) {
            Map<String, Object> response = new HashMap<>();
            response.put("verify", true);
            response.put("message", "Token is valid");
            return ResponseEntity.ok(response); // 返回驗證成功的響應
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("verify", false);
            response.put("message", "Token is invalid or expired");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // 返回驗證失敗的響應
        }
    }
}
