package ch.csnc.payload;

public class Payload {
    public Boolean isActive;
    public PayloadType type;
    public String key;
    public String value;

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

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setType(PayloadType type) {
        this.type = type;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
