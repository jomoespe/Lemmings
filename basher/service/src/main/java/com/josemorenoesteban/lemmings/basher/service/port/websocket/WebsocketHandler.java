package com.josemorenoesteban.lemmings.basher.service.port.websocket;

import static java.lang.String.format;
import static java.util.logging.Level.*;
import static java.util.logging.Logger.*;

import com.josemorenoesteban.lemmings.basher.service.Main;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class WebsocketHandler {
    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
    private final Thread worker;
    
    public WebsocketHandler() {
        getLogger(WebsocketHandler.class.getName()).log(INFO, () -> format("Creating Basher websocketworker") );
        worker = new Thread( () -> {
            Main.dataSupplier().forEach(message -> {
                getLogger(WebsocketHandler.class.getName()).log(FINEST, () -> format("Generating message [%s] to sesisons. Total sessions=%s", message, sessions.size()) );
                sessions.forEach( session -> {
                    try {
                        session.getRemote().sendString( message );
                    } catch(IOException e) {
                        getLogger(WebsocketHandler.class.getName()).log(SEVERE, e, () -> format("IOException sending message to %s session. Closing it.", session.getRemoteAddress()));
                        session.close();
                    }
                });
            });
        });
        getLogger(WebsocketHandler.class.getName()).log(INFO, () -> format("Basher websocket worker created") );
        worker.start();
        getLogger(WebsocketHandler.class.getName()).log(INFO, () -> format("Basher websocket worker started") );
    }

    @OnWebSocketConnect
    public void connected(Session session) {
        getLogger(WebsocketHandler.class.getName()).log(INFO, () -> format("client connected from %s", session.getRemoteAddress()) );
        sessions.add(session);
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        getLogger(WebsocketHandler.class.getName()).log(INFO, () -> format("client %s close session with reason %s and status code %s", session.getRemoteAddress(), reason, statusCode ));
        sessions.remove(session);
    }

    @OnWebSocketMessage
    public void echo(Session session, String message) throws IOException {
        getLogger(WebsocketHandler.class.getName()).log(INFO, () -> format("Got and re-sending message [%s] to client %s", message, session.getRemoteAddress() ));
        session.getRemote().sendString(message);
    }
}
