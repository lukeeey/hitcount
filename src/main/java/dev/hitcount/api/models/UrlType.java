package dev.hitcount.api.models;

public enum UrlType {
    UNKNOWN,
    GITHUB,
    GITLAB,
    OTHER;

    public static UrlType fromId(int id) {
        for (UrlType type : values()) {
            if (type.ordinal() == id) {
                return type;
            }
        }
        return UrlType.UNKNOWN;
    }
}
