package com.sapog87.visual_novel.app.repository;

import com.sapog87.visual_novel.app.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}