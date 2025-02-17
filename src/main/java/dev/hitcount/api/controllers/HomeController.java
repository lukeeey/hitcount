package dev.hitcount.api.controllers;

import io.javalin.http.Context;

public class HomeController {

    public void render(Context ctx) {
        ctx.render("/web/index.ext");
    }
}
