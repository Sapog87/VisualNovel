package com.sapog87.visual_novel.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private Long chatId;

    @Column
    private Integer messageId;

    @Column(length = 1000)
    private String lastMessageText;

    @Column
    private String nodeId;

    @OneToOne
    private User user;
}
