package com.sapog87.visual_novel.app.service;

import com.sapog87.visual_novel.app.entity.Token;
import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.app.exception.UserNotFoundException;
import com.sapog87.visual_novel.app.repository.TokenRepository;
import com.sapog87.visual_novel.app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Random;

@Service
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final Random random;

    public TokenService(TokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        try {
            random = SecureRandom.getInstance("DRBG");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String createToken(Long userId) {
        String token = this.generateToken();
        User user = userRepository.findUserByExternalUserId(userId).orElseThrow(UserNotFoundException::new);
        Token tokenEntity = new Token(token, user, LocalDateTime.now().plusHours(1));
        tokenRepository.save(tokenEntity);
        return token;
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public boolean validateToken(String token) {
        Token tokenEntity = tokenRepository.findByToken(token);
        return tokenEntity != null && tokenEntity.getExpiryDate().isAfter(LocalDateTime.now());
    }

    public User getUserFromToken(String token) {
        Token tokenEntity = tokenRepository.findByToken(token);
        log.info(tokenEntity.toString());
        return userRepository.findUserByExternalUserId(tokenEntity.getUser().getExternalUserId()).orElseThrow(UserNotFoundException::new);
    }
}
