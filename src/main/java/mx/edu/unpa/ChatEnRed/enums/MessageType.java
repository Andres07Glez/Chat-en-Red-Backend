package mx.edu.unpa.ChatEnRed.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MessageType {
    TEXT("text"),
    FILE("file"),
    SYSTEM("system");

    private final String dbValue;
    MessageType(String dbValue) { this.dbValue = dbValue; }

    @JsonValue
    public String getDbValue() { return dbValue; }

    @JsonCreator
    public static MessageType fromValue(String value) {
        if (value == null) return null;
        for (MessageType t : values()) {
            if (t.dbValue.equalsIgnoreCase(value) || t.name().equalsIgnoreCase(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown MessageType: " + value);
    }
}
