package com.sapog87.visual_novel.app.entity;

import com.sapog87.visual_novel.app.repository.converter.TypeConverter;
import com.sapog87.visual_novel.core.parser.SemanticType;
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
    private Boolean permanent;

    @Column
    private String value;

    @Column
    @Convert(converter = TypeConverter.class)
    private SemanticType type;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Object getValue() {
        return switch (type) {
            case STRING -> value;
            case REAL -> Double.valueOf(value);
            case INT -> Integer.valueOf(value);
            case BOOL -> {
                Boolean bool = Boolean.valueOf(value);
                if (!bool.toString().equalsIgnoreCase(value)){
                    //TODO создать VariableIllegalTypeException
                    throw new RuntimeException("VariableIllegalTypeException");
                }
                yield bool;
            }
            case UNDEF -> null;
        };
    }

    public void setValue(Object value) {
        if (!checkType(value.toString())) {
            //TODO создать VariableIllegalTypeException
            throw new RuntimeException("VariableIllegalTypeException");
        }
        this.value = value.toString();
    }

    private boolean checkType(String value) {
        if (Boolean.valueOf(value).toString().equalsIgnoreCase(value))
            return type.equals(SemanticType.BOOL) || type.equals(SemanticType.STRING);

        try {
            Integer.parseInt(value);
            return type.equals(SemanticType.INT) || type.equals(SemanticType.REAL);
        } catch (NumberFormatException ignored) {
        }

        try {
            Double.parseDouble(value);
            return type.equals(SemanticType.REAL);
        } catch (NumberFormatException ignored) {
        }

        return type.equals(SemanticType.STRING);
    }
}
