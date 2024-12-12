package com.backtestpro.btp.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.backtestpro.btp.pojo.AppUser;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "jkh4aliuejr34h352251jk34"; // 設置你的密鑰
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 設置過期時間 1 小時

    public static String generateToken(Optional<AppUser> userOptional) {
        return userOptional.map(user -> {
            // 將用戶的角色轉換為逗號分隔的字串
            String roles = user.getRoles().stream()
                .collect(Collectors.joining(","));
    
            return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("roles", roles)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
        }).orElseThrow(() -> new RuntimeException("使用者不存在"));
    }

    public static Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public static String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public static String extractRoles(String token) {
        return (String) extractClaims(token).get("roles");
    }

    public static boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenSignatureValid(String token) {
    try {
        // 使用 secret 或 public key 驗證 JWT 簽名
        Jws<Claims> claims = Jwts.parser()
            .setSigningKey(SECRET_KEY)  // 這裡 SECRET_KEY 是你用來簽名的密鑰
            .parseClaimsJws(token);
        return true;
    } catch (JwtException e) {
        return false;  // Token 不合法，簽名無效
    }
    }

    public boolean validateToken(String token) {
        return (isTokenSignatureValid(token) && !isTokenExpired(token));
    }
}
