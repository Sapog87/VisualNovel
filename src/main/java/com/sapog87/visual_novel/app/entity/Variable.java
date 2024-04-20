package com.sapog87.visual_novel.app.entity;

import com.sapog87.visual_novel.app.exception.VariableIllegalTypeException;
import com.sapog87.visual_novel.app.repository.converter.TypeConverter;
import com.sapog87.visual_novel.core.parser.SemanticType;
import com.sapog87.visual_novel.core.story.nodes.variable.VariableInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    public Variable(VariableInfo info, User user) {
        this.setName(info.getName());
        this.setType(info.getType());
        this.setValue(info.getValue());
        this.setPermanent(info.getPermanent());
        this.setUser(user);
    }

    public Object getValue() {
        return switch (type) {
            case STRING -> value;
            case REAL -> Double.valueOf(value);
            case INT -> Integer.valueOf(value);
            case BOOL -> {
                boolean bool = Boolean.parseBoolean(value);
                if (!Boolean.toString(bool).equalsIgnoreCase(value)){
                    throw new VariableIllegalTypeException("Value is not a Boolean type");
                }
                yield bool;
            }
            case UNDEF -> null;
        };
    }

    public void setValue(Object value) {
        if (!this.checkType(value.toString())) {
            throw new VariableIllegalTypeException("Unacceptable type");
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
