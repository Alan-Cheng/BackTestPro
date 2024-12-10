package com.backtestpro.btp.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import com.backtestpro.btp.repository.UserRepository;
import com.backtestpro.btp.pojo.AppUser;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 從資料庫中查找用戶
        AppUser appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 返回 Spring Security 的 UserDetails
        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword()) // 資料庫中應存加密密碼
                .roles(appUser.getRoles().toArray(new String[0])) // 用戶角色
                .build();
    }
}
