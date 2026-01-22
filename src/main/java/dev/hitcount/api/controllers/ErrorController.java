package dev.hitcount.api.controllers;

import dev.hitcount.api.exceptions.GenericServerErrorException;
import dev.hitcount.api.models.ErrorResponse;
import io.javalin.http.Context;

public class ErrorController {
    private static final String NOT_FOUND_SVG = "<svg xmlns='http://www.w3.org/2000/svg' width='97.15625' height='20' id='flat-square'> <rect width='63.1640625' height='20' fill='#555555'></rect> <rect x='63.1640625' width='33.9921875' height='20' fill='#cc113f'></rect> <g fill='#fff' text-anchor='middle' font-size='11' font-family='DejaVu Sans,Verdana,Geneva,sans-serif'> <text x='31.58203125' y='14'>not found</text> <text x='80.16015625' y='14'>404</text> </g> </svg>";
    private static final String SERVER_ERROR_SVG = "<svg xmlns='http://www.w3.org/2000/svg' width='109.8525390625' height='20' id='flat-square'> <rect width='75.8603515625' height='20' fill='#555555'></rect> <rect x='75.8603515625' width='33.9921875' height='20' fill='#ff124c'></rect> <g fill='#fff' text-anchor='middle' font-size='11' font-family='DejaVu Sans,Verdana,Geneva,sans-serif'> <text x='37.93017578125' y='14'>server error</text> <text x='92.8564453125' y='14'>500</text> </g> </svg>";

    public void handleNotFound(Context ctx) {
        if (ctx.path().endsWith(".svg")) {
            setCacheHeaders(ctx);
            ctx.header("Content-Type", "image/svg+xml").result(NOT_FOUND_SVG);
            return;
        }
        if (ctx.path().endsWith(".json")) {
            ctx.json(new ErrorResponse("Not found"));
            return;
        }
        ctx.render("/web/notfound.ftl");
    }

    public void handleServerError(Context ctx, Throwable ex) {
        if (ex instanceof GenericServerErrorException) {
            ctx.status(((GenericServerErrorException) ex).getStatusCode());
        } else {
            ctx.status(500);
        }
        if (ctx.path().endsWith(".svg")) {
            setCacheHeaders(ctx);
            ctx.header("Content-Type", "image/svg+xml").result(SERVER_ERROR_SVG);
            return;
        }
        if (ctx.path().endsWith(".json")) {
            ctx.json(new ErrorResponse(ex != null ? ex.getMessage() : "An unknown error has occurred"));
            return;
        }
        ctx.render("/web/servererror.ftl");
    }

    private void setCacheHeaders(Context ctx) {
        ctx.header("Content-Type", "image/svg+xml");
        ctx.header("Cache-Control", "no-cache, no-store, must-revalidate");
        ctx.header("Pragma", "no-cache");
        ctx.header("Expires", "0");
    }
}