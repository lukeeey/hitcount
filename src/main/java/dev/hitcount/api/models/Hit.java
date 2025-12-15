package dev.hitcount.api.models;

import lombok.Data;

@Data
public class Hit {
    private final String createdAt;
    private final String timeAgo;
    private final PathType pathType;
}
