package com.sapog87.visual_novel.spring.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "`user`", indexes = @Index(columnList = "telegramUserId"))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private Long telegramUserId;

    @Column
    private String storyNodeId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @MapKey(name = "name")
    private Map<String, Variable> variables;

}
