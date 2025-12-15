package dev.hitcount.api.controllers;

import com.google.gson.JsonObject;
import dev.hitcount.api.SocketHandler;
import dev.hitcount.api.svg.SvgElement;
import dev.hitcount.api.database.MySQLConnection;
import dev.hitcount.api.models.PathData;
import dev.hitcount.api.models.PathType;
import dev.hitcount.api.models.ProjectData;
import dev.hitcount.api.svg.SvgUtils;
import io.javalin.http.Context;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ProjectController {
    private final MySQLConnection connection;
    private final SocketHandler socketHandler;

    public void handleGetBrowser(Context ctx) {
        String path = normalizePath(ctx.path());
        PathData data = connection.getPathData(path);

        if (data == null) {
            ctx.status(404).result();
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("path", path);
        params.put("totalHits", data.getTotalHits());
        params.put("hitsThisMonth", data.getHitsThisMonth());
        params.put("hitsToday", data.getHitsToday());
//        params.put("globalRank", data.getGlobalRank());
//        params.put("urlType", data.getUrlType());

        ctx.render("/web/project.ext", params);
    }

    public void handleGetJson(Context ctx) {
        String path = normalizePath(ctx.path());
        PathData hits = connection.getPathData(path);

        if (hits == null) {
            ctx.status(404).result();
            return;
        }

        ProjectData data = new ProjectData(path, hits);
        ctx.json(data);
    }

    public void handleGetShieldsJson(Context ctx) {
        PathType pathType = retrievePathType(ctx.path());
        String path = normalizePath(ctx.path());

        connection.logHit(path, pathType);
        int hitCount = connection.getHitCount(path);

        SvgElement svg = SvgUtils.createSvg(ctx, hitCount);
        JsonObject root = new JsonObject();

        root.addProperty("color", svg.getLabelColor());
        root.addProperty("label", svg.getLabel());
        root.addProperty("message", String.valueOf(hitCount));
        root.addProperty("schemaVersion", 1);
        root.addProperty("style", "flat");

        ctx.json(root);
    }

    public void handleGetSvg(Context ctx) {
        PathType pathType = retrievePathType(ctx.path());
        String path = normalizePath(ctx.path());

        connection.logHit(path, pathType);
        int hitCount = connection.getHitCount(path);

        socketHandler.broadcastHit(path, hitCount);

        SvgElement svg = SvgUtils.createSvg(ctx, hitCount);
        ctx.header("Content-Type", "image/svg+xml");
        ctx.header("Cache-Control", "no-cache, no-store, must-revalidate");
        ctx.header("Pragma", "no-cache");
        ctx.header("Expires", "0");
        ctx.result(svg.create());
    }

    private PathType retrievePathType(String path) {
        if (path.endsWith(".svg")) return PathType.SVG;
        if (path.endsWith(".shields.json")) return PathType.SHIELDS_JSON;
        if (path.endsWith(".json")) return PathType.JSON;
        return PathType.SVG;
    }

    private String normalizePath(String path) {
        return path.substring(3)
                .replaceAll(".svg", "")
                .replaceAll(".shields.json", "")
                .replaceAll(".json", "");
    }
}
