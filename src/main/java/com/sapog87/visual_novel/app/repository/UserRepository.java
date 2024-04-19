package com.sapog87.visual_novel.app.repository;

import com.sapog87.visual_novel.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByExternalUserId(Long id);

    @Query("select u from User u join fetch Message m on u.lastMessage = m where u.id = :id")
    Optional<User> findUserByExternalUserIdWithMessage(@Param("id") Long id);
}
