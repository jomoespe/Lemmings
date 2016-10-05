package com.josemorenoesteban.lemmings.basher.service;


import static spark.Spark.stop;

import com.josemorenoesteban.lemmings.basher.service.port.websocket.WebsocketHandler;
import com.josemorenoesteban.lemmings.basher.service.port.websocket.WebsocketPort;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Main {
    private static final ThreadLocal<Stream<String>> threadLocal = new ThreadLocal<Stream<String>>(){
        @Override
        protected Stream<String> initialValue() {
            return Stream.generate(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Logger.getLogger(WebsocketHandler.class.getName()).log(Level.SEVERE, null, e);
                }
                return "this message is generated at " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            });        
        }
    };

    public static void main(String...args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> { 
            System.out.println("Stopping al HTTP routes");
            stop(); 
            System.out.println("HTTP routes stoped");
        }, "Basher shutdown hook"));
        WebsocketPort port = new WebsocketPort( );
    }
    
    public static Stream<String> dataSupplier() {
        return threadLocal.get();
    }
}
