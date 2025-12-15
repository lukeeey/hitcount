package dev.hitcount.api.models;

public enum PathType {
    SVG,
    JSON,
    SHIELDS_JSON,
    REGISTER_PATH;

    public static PathType from(int ordinal) {
        for (PathType type : values()) {
            if (type.ordinal() == ordinal) {
                return type;
            }
        }
        return null;
    }
}
