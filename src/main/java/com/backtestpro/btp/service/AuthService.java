package com.backtestpro.btp.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.backtestpro.btp.util.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

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

        // 提取 username
        String username = jwtUtil.extractUsername(token);

        // 驗證 token 是否有效
        if (jwtUtil.validateToken(token, username)) {
            response.put("verify", true);
            response.put("message", "Token is valid");
        } else {
            response.put("verify", false);
            response.put("message", "Token is invalid or expired");
        }

        return response;
    }
}
