package com.josemorenoesteban.lemmings.basher.service.port.websocket;

import spark.Spark;
import static spark.Spark.port;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

public class WebsocketPort {
    public WebsocketPort( ) {
        port(9090);
        staticFiles.location("/web");
        webSocket("/v1/basher", WebsocketHandler.class);
        Spark.init();
    }
}
