package com.sapog87.visual_novel.spring.repository;

import com.sapog87.visual_novel.spring.repository.entity.Variable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryVariableRepository extends JpaRepository<Variable, Long> {}
