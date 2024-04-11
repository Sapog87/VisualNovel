package com.sapog87.visual_novel.app.repository.converter;

import com.sapog87.visual_novel.core.parser.SemanticType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TypeConverter implements AttributeConverter<SemanticType, String> {
    @Override
    public String convertToDatabaseColumn(SemanticType attribute) {
        return String.valueOf(attribute);
    }

    @Override
    public SemanticType convertToEntityAttribute(String dbData) {
        return SemanticType.valueOf(dbData);
    }
}
