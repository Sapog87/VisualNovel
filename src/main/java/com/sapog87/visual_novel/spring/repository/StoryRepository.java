package com.sapog87.visual_novel.spring.repository;

import com.sapog87.visual_novel.spring.repository.entity.VNUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<VNUser, Long> {}
