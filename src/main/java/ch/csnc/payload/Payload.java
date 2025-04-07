package ch.csnc.payload;

import burp.api.montoya.persistence.PersistedObject;

import java.io.Serializable;
import java.util.Objects;

public class Payload implements Serializable {
    public Boolean isActive;
    public PayloadType type;
    public String key;
    public String value;

    private static final String separator = "#";

    public Payload(Boolean isActive, PayloadType type, String key, String value) {
        this.isActive = isActive;
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public Boolean isActive() {
        return isActive;
    }

    public PayloadType getType() {
        return type;
    }

    public void setType(PayloadType type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    /**
     * Provide a way to serialize into a String so that it can be stored to persistent preferences
     * @return
     */
    public String toString() {
        String out = "";
        out += (isActive) ? "1" : "0";
        out += separator;
        out += type.name();
        out += separator;
        out += key;
        out += separator;
        out += value;

        assert out.split(separator).length == 4;

        return out;
    }

    public static Payload fromString(String serialized) {
        String[] components = serialized.split(separator);
        assert components.length == 4;

        boolean isActive = Objects.equals(components[0], "1");
        PayloadType payloadType = PayloadType.valueOf(components[1]);
        return new Payload(isActive, payloadType, components[2], components[3]);
    }
}
