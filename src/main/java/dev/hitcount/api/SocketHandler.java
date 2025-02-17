package dev.hitcount.api;

import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SocketHandler {
    private Set<WsContext> activeConnections = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void handle(WsConfig ws) {
        ws.onConnect(ctx -> {
            activeConnections.add(ctx);
            System.out.println("Connected: " + ctx.sessionId());
        });

        ws.onClose(ctx -> {
            activeConnections.remove(ctx);
            System.out.println("Disconnected: " + ctx.sessionId());
        });

        ws.onMessage(ctx -> {
            System.out.println("Received: " + ctx.message());
        });
    }

    public void broadcastHit(String path, int hitCount) {
        broadcast(path + ";" + hitCount);
    }

    public void broadcast(String message) {
        activeConnections.forEach(wsContext -> {
            if (wsContext.session.isOpen()) {
                wsContext.send(message);
            }
        });
    }
}
