package com.sapog87.visual_novel.spring.repository.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ObjectConverter implements AttributeConverter<Object, String> {
    @Override
    public String convertToDatabaseColumn(Object attribute) {
        return String.valueOf(attribute);
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        try {
            return Integer.valueOf(dbData);
        } catch (NumberFormatException ignored) {
        }

        try {
            return Double.valueOf(dbData);
        } catch (NumberFormatException ignored) {
        }

        if (dbData.equalsIgnoreCase("true"))
            return true;
        else if (dbData.equalsIgnoreCase("false"))
            return false;

        return dbData;
    }
}
