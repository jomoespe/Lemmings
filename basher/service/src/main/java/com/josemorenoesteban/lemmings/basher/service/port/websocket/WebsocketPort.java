package com.josemorenoesteban.lemmings.basher.service.port.websocket;

import static spark.Spark.init;
import static spark.Spark.port;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

public class WebsocketPort {
    private static final String SERVICE_URI           = "/v1/basher";
    private static final String STATIC_CONTENT_FOLDER = "/web";

    public WebsocketPort() {
        port(9090);
        staticFiles.location(STATIC_CONTENT_FOLDER);
        webSocket(SERVICE_URI, WebsocketHandler.class);
        init();
    }
}
