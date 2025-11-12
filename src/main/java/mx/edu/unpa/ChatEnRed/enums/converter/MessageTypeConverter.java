package mx.edu.unpa.ChatEnRed.enums.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import mx.edu.unpa.ChatEnRed.enums.MessageType;

@Converter(autoApply = true)
public class MessageTypeConverter implements AttributeConverter<MessageType, String> {

    @Override
    public String convertToDatabaseColumn(MessageType attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public MessageType convertToEntityAttribute(String dbData) {
        return MessageType.fromValue(dbData);
    }
}