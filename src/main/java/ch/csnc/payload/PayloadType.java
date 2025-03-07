package ch.csnc.payload;

public enum PayloadType {
    PARAM("URL Parameter"),
    HEADER("HTTP Header");

    public final String label;

    PayloadType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }

}