package com.sapog87.visual_novel.spring.repository.entity;

import com.sapog87.visual_novel.spring.repository.converter.ObjectConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Variable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private String name;

    @Column
    @Convert(converter = ObjectConverter.class)
    private Object value;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private VNUser user;
}
