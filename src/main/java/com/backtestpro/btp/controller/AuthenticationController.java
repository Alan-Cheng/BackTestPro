package com.backtestpro.btp.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.security.core.AuthenticationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.backtestpro.btp.dto.AuthRequest;
import com.backtestpro.btp.repository.UserRepository;
import com.backtestpro.btp.service.AuthService;
import com.backtestpro.btp.pojo.AppUser;

@RestController
@RequestMapping("${app.api-prefix}/authenticate")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest authRequest) {
        // 檢查帳號是否存在
        if (!userRepository.existsByUsername(authRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "帳號不存在，請先註冊"));
        }

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
                    .body(Collections.singletonMap("message", "密碼錯誤，請重新再試"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody AuthRequest authRequest) {
        try {
            // 檢查用戶名是否已經存在
            if (userRepository.existsByUsername(authRequest.getUsername())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Collections.singletonMap("message", "帳號已存在，請重新輸入"));
            }
    
            // 創建新用戶並保存到資料庫
            AppUser newUser = new AppUser();
            newUser.setUsername(authRequest.getUsername());
            newUser.setPassword(authRequest.getPassword());
            userRepository.save(newUser);
    
            // 回傳成功訊息
            Map<String, String> responseData = new HashMap<>();
            responseData.put("message", "註冊成功，請重新登入");
            responseData.put("status", String.valueOf(HttpStatus.CREATED.value()));
            return ResponseEntity.status(HttpStatus.CREATED).body(responseData);
    
        } catch (Exception e) {
            // 捕獲異常並返回伺服器錯誤
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "發生錯誤，請稍後再試"));
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
