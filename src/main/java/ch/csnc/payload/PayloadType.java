package ch.csnc.payload;

public enum PayloadType {
    PARAM("URL Parameter"),
    HEADER("HTTP Header"),
    INVALID("Invalid");

    public final String label;

    PayloadType(String label) {
        this.label = label;
    }
}