package dev.hitcount.api.models;

import lombok.Data;

@Data
public class ProjectData {
    private final String path;
    private final PathData hits;

}
