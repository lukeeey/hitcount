package dev.hitcount.api.models;

import lombok.Data;

@Data
public class PathData {
    private final int totalHits;
    private final int hitsThisMonth;
    private final int hitsToday;
    private final int globalRank;
    private final UrlType urlType;
}
