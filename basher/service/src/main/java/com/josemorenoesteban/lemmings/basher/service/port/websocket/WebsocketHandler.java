package com.josemorenoesteban.lemmings.basher.service.port.websocket;

import com.josemorenoesteban.lemmings.basher.service.Main;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebSocket
public class WebsocketHandler {
    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    public WebsocketHandler() {
        new Thread(() -> {
            Main.dataSupplier().forEach(message -> {
                sessions.forEach( session -> {
                    try {
                        session.getRemote().sendString( message );
                    } catch(IOException e) {
                        Logger.getLogger(WebsocketHandler.class.getName()).log(Level.SEVERE, "IOException sending message to " + session.getRemoteAddress() + " session. Closing it.", e);
                        session.close();
                    }
                });
            });
        }).start();
    }

    @OnWebSocketConnect
    public void connected(Session session) {
        Logger.getLogger(WebsocketHandler.class.getName()).log(Level.INFO, () -> "client connected from " + session.getRemoteAddress() );
        sessions.add(session);
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        Logger.getLogger(WebsocketHandler.class.getName()).log(Level.INFO, () -> "client " + session.getRemoteAddress() + " close session");
        sessions.remove(session);
    }

    //@OnWebSocketMessage
    //public void echo(Session session, String message) throws IOException {
    //  System.out.println("Got: " + message);
    //  session.getRemote().sendString(message);
    //}
}
