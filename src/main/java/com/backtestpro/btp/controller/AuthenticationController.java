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

    // @PostMapping("/verify-token")
    // public ResponseEntity<Map<String, Object>>
    // verifyToken(@RequestHeader("Authorization") String authorizationHeader) {
    // // 調用 service 層來處理驗證邏輯和生成 response
    // Map<String, Object> response =
    // authService.validateAndGenerateResponse(authorizationHeader);

    // // 根據驗證結果返回響應
    // if ((boolean) response.get("verify")) {
    // return ResponseEntity.ok(response); // 返回驗證成功的響應
    // } else {
    // return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); //
    // 返回驗證失敗的響應
    // }
    // }

    @PostMapping("/verify-token")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestHeader("Authorization") String authorizationHeader) {
        // 創建回應的 Map，設定 verify 為 true
        Map<String, Object> response = new HashMap<>();

        // 由於過濾器會處理 JWT 的驗證，若能進入此處，則表示 token 是有效的
        response.put("verify", true);
        response.put("message", "Token is valid");

        // 返回驗證成功的響應
        return ResponseEntity.ok(response);
    }
}
