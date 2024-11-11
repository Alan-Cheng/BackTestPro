package com.backtestpro.btp.filter;

import java.util.Collection;

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

        // 如果 username 存在且 SecurityContext 尚未設置認證
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 驗證 token 是否有效
            if (jwtUtil.validateToken(token, username)) {
                // 提取角色或權限
                String role = jwtUtil.extractRole(token);  // 假設 "role" 是從 token 中提取的角色
                Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_" + role);  // 根據角色創建權限列表

                // 創建一個身份認證令牌
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);  // 將 authorities 設置到 authenticationToken 中

                // 設置認證信息到 SecurityContext 中
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // 繼續處理請求
        chain.doFilter(request, response);
    }
}
