package com.sapog87.visual_novel.app.entity;

import com.sapog87.visual_novel.core.story.Story;
import jakarta.persistence.*;
import lombok.Builder;
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

    @OneToOne(mappedBy = "user")
    private Message lastMessage;

    @Version
    private Integer version;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @MapKey(name = "name")
    private Map<String, Variable> variables;
}
