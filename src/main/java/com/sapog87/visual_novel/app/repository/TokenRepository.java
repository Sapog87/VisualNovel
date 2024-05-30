package com.sapog87.visual_novel.app.repository;

import com.sapog87.visual_novel.app.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, String> {
    Token findByToken(String token);
}
