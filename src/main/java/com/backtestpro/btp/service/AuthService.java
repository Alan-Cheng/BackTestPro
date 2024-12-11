package com.backtestpro.btp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.backtestpro.btp.dto.AuthRequest;
import com.backtestpro.btp.pojo.AppUser;
import com.backtestpro.btp.repository.UserRepository;
import com.backtestpro.btp.util.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    public String authenticateAndGenerateJwt(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        if (authentication.isAuthenticated()) {
            return jwtUtil.generateToken(username);
        } else {
            return null;
        }
    }

    public Map<String, Object> validateAndGenerateResponse(String authorizationHeader) {
        Map<String, Object> response = new HashMap<>();

        // 檢查 Authorization header 是否存在且以 "Bearer " 開頭
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.put("verify", false);
            response.put("message", "Authorization header is missing or incorrect");
            return response;
        }

        // 提取 token
        String token = authorizationHeader.substring(7);

        // 驗證 token 是否有效
        if (jwtUtil.validateToken(token)) {
            response.put("verify", true);
            response.put("message", "Token is valid");
        } else {
            response.put("verify", false);
            response.put("message", "Token is invalid or expired");
        }

        return response;
    }

    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest authRequest){
        // 檢查帳號是否存在
        if (!userRepository.existsByUsername(authRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "帳號不存在，請先註冊"));
        }

        try {
            // 認證並生成 JWT token
            String token = authenticateAndGenerateJwt(authRequest.getUsername(), authRequest.getPassword());

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

    public ResponseEntity<Map<String, String>> register(@RequestBody AuthRequest authRequest){
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
}
