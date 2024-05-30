package com.sapog87.visual_novel.app.security;

import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.app.exception.UserNotFoundException;
import com.sapog87.visual_novel.app.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String eid) {
        User user = userRepository.findUserByExternalUserId(Long.valueOf(eid)).orElseThrow(UserNotFoundException::new);
        return new org.springframework.security.core.userdetails.User(user.getExternalUserId().toString(), "", List.of(new SimpleGrantedAuthority("USER")));
    }
}