package com.sapog87.visual_novel.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "`user`", indexes = @Index(columnList = "externalUserId"))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private Long externalUserId;

    @Column
    private String storyNodeId;

    @Column
    private Long chatId;

    @Column
    private Integer messageId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @MapKey(name = "name")
    private Map<String, Variable> variables;

}
