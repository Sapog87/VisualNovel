package com.sapog87.visual_novel.spring.repository;

import com.sapog87.visual_novel.spring.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoryUserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByTelegramUserId(Long id);
}
