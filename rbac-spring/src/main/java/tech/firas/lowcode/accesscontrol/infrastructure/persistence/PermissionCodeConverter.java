package tech.firas.lowcode.accesscontrol.infrastructure.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import tech.firas.lowcode.accesscontrol.domain.model.PermissionCode;

@Converter(autoApply = true)
public class PermissionCodeConverter implements AttributeConverter<PermissionCode, String> {

    @Override
    public String convertToDatabaseColumn(PermissionCode attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public PermissionCode convertToEntityAttribute(String dbData) {
        return dbData == null ? null : PermissionCode.of(dbData);
    }
}
