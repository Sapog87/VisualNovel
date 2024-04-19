package com.sapog87.visual_novel.app.repository;

import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.app.entity.Variable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariableRepository extends JpaRepository<Variable, Long> {
    List<Variable> findAllByUser(User user);

    Variable findByName(String name);
}
