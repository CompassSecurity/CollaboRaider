package ch.csnc.payload;


public class Payload {
    private static final String separator = ",";
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

    /**
     * Create a payload object from a String
     *
     * @param serialized String representation of the payload as returned by toString
     * @return Deserialized Payload object
     */
    public static Payload fromString(String serialized) {
        boolean isActive = true;

        // In imported files, inactive parameters are marked with '#'
        if (serialized.startsWith("#")) {
            isActive = false;
            serialized = serialized.substring(1);
        }
        // Otherwise, check if the string starts with a zero or one
        else if (serialized.startsWith("0") || serialized.startsWith("1")) {
            isActive = serialized.charAt(0) == '1';
            serialized = serialized.substring(2);
        }

        String[] components = serialized.split(separator, 3);
        assert components.length == 3;

        PayloadType payloadType = PayloadType.valueOf(components[0].toUpperCase());
        return new Payload(isActive, payloadType, components[1], components[2]);
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
     *
     * @return String representation of the Payload object
     */
    public String toString() {
        String out = "";
        out += (isActive) ? "1" : "0";
        out += separator;
        out += type.name().toLowerCase();
        out += separator;
        out += key;
        out += separator;
        out += value;

        assert out.split(separator).length == 4;

        return out;
    }
}
