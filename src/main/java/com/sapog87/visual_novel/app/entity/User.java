package com.sapog87.visual_novel.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    @Version
    private Integer version;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @MapKey(name = "name")
    private Map<String, Variable> variables;
}
