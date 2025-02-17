package dev.hitcount.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.hitcount.api.controllers.ErrorController;
import dev.hitcount.api.controllers.HomeController;
import dev.hitcount.api.controllers.ProjectController;
import dev.hitcount.api.database.MySQLConnection;
import dev.hitcount.api.exceptions.GenericServerErrorException;
import dev.hitcount.api.models.PathType;
import dev.hitcount.api.models.RegisterPathData;
import dev.hitcount.api.models.UrlType;
import io.javalin.Javalin;
import io.javalin.json.JsonMapper;
import io.javalin.rendering.template.JavalinFreemarker;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class Server {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    private final HomeController homeController;
    private final ProjectController projectController;
    private final ErrorController errorController;

    private final MySQLConnection connection;
    private final SocketHandler socketHandler;

    public Server() {
        this.connection = new MySQLConnection();
        this.socketHandler = new SocketHandler();
        this.homeController = new HomeController();
        this.projectController = new ProjectController(connection, socketHandler);
        this.errorController = new ErrorController();
    }

    public static void main(String[] args) {
        new Server().startup();
    }

    public void startup() {
        JsonMapper gsonMapper = new JsonMapper() {
            @Override
            public String toJsonString(@NotNull Object obj, @NotNull Type type) {
                return GSON.toJson(obj, type);
            }

            @Override
            public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
                return GSON.fromJson(json, targetType);
            }
        };
        Javalin app = Javalin.create(servlet -> {
            servlet.staticFiles.add("/web");
            servlet.fileRenderer(new JavalinFreemarker());
            servlet.jsonMapper(gsonMapper);
        }).start(8037);

        app.before(ctx -> {
            ctx.header("Server", "HitCount.dev");
        });

        app.get("/", homeController::render);
        app.get("/p/*.svg", projectController::handleGetSvg);
        app.get("/p/*.shields.json", projectController::handleGetShieldsJson);
        app.get("/p/*.json", projectController::handleGetJson);
        app.get("/p/*", projectController::handleGetBrowser);
        app.ws("/socket", socketHandler::handle);
        app.post("/registerPathData", ctx -> {
            RegisterPathData data = ctx.bodyAsClass(RegisterPathData.class);

            // We only register it once.
            // We need to log a hit so that it won't return a 404 when viewing the project page on hitcount.dev
            if (connection.getPathCreationData(data.getPath()) == -1) {
                connection.registerPathData(data.getPath(), UrlType.fromId(data.getUrlType()));
                connection.logHit(data.getPath(), PathType.REGISTER_PATH);
            }
            ctx.result();
        });
        app.get("/health", ctx -> {
            boolean databaseOnline = connection.testConnection();

            JsonObject object = new JsonObject();
            object.addProperty("online", true);
            object.addProperty("database", databaseOnline);

            ctx.status(databaseOnline ? 200 : 500).json(object);
        });

        app.error(404, errorController::handleNotFound);
        app.error(500, ctx -> errorController.handleServerError(ctx, null));
        app.exception(GenericServerErrorException.class, (e, ctx) -> errorController.handleServerError(ctx, e));
    }
}
