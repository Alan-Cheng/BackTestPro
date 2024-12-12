package com.backtestpro.btp.filter;

import java.util.Collection;
import java.util.Arrays;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.backtestpro.btp.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 如果 Authorization 標頭存在且以 "Bearer " 開頭，則提取 token 和 username
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);  // 去除 "Bearer " 前綴
            username = jwtUtil.extractUsername(token);  // 從 token 中提取 username
        }

        // 如果 username 存在代表有傳來JWT，隨之更新權限
        if (username != null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 驗證 token 是否有效
            if (jwtUtil.validateToken(token)) {
                // 提取角色或權限
                String rolesString = jwtUtil.extractRoles(token);
                
                // 增加調試日誌
                System.out.println("Token roles raw string: " + rolesString);

                if (rolesString == null || rolesString.isEmpty()) {
                    System.out.println("No roles found in token for user: " + username);
                    chain.doFilter(request, response);
                    return;
                }

                String[] roles = rolesString.split(",");

                Collection<? extends GrantedAuthority> authorities = 
                AuthorityUtils.createAuthorityList(
                    Arrays.stream(roles)
                        .map(role -> "ROLE_" + role.trim())
                        .toArray(String[]::new)
                );

                // 額外檢查是否包含 VIP 角色
                boolean hasVipRole = authorities.stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_VIP"));

                System.out.println("User: " + username + ", Roles: " + Arrays.toString(roles));
                System.out.println("Has VIP role: " + hasVipRole);

                // 創建一個身份認證令牌
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, authorities);

                // 設置認證信息到 SecurityContext 中
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                if (!hasVipRole) {
                    System.out.println("User does not have VIP role: " + username);
                }
            }
        }

        System.out.println("目前權限: " + SecurityContextHolder.getContext().getAuthentication());

        // 繼續處理請求
        chain.doFilter(request, response);
    }
}
