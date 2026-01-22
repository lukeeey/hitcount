package dev.hitcount.api.models;

import lombok.Data;

@Data
public class LeaderboardItem {
    private final String path;
    private final int hits;
}
