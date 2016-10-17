package com.josemorenoesteban.lemmings.basher.service;

import static spark.Spark.stop;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;

import com.josemorenoesteban.lemmings.basher.service.port.websocket.WebsocketHandler;
import com.josemorenoesteban.lemmings.basher.service.port.websocket.WebsocketPort;

import java.util.stream.Stream;

public class Main {
    private static final long TIMEOUT = 1_000;
    
    public static void main(String...args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> { 
            System.out.println("Stopping al HTTP routes");
            stop(); 
            System.out.println("HTTP routes stoped");
        }, "Basher shutdown hook"));
        WebsocketPort port = new WebsocketPort();
    }

    public static Stream<String> dataSupplier() {
        return threadLocal.get();
    }
    
    private static final ThreadLocal<Stream<String>> threadLocal = new ThreadLocal<Stream<String>>() {
        @Override
        protected Stream<String> initialValue() {
            return Stream.generate( () -> {
                try {
                    Thread.sleep( TIMEOUT );
                } catch (InterruptedException e) {
                    getLogger(WebsocketHandler.class.getName()).log(SEVERE, null, e);
                }
                return format("Basher service. Generated at %s", now().format(ISO_DATE_TIME));
            });        
        }
    };
}
