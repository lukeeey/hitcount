package dev.hitcount.api.models;

import lombok.Data;

@Data
public class RegisterPathData {
    private final String path;
    private final int urlType;
}