package com.josemorenoesteban.lemmings.basher.client;

import com.josemorenoesteban.lemmings.basher.client.v1.Basher;
import java.util.Map;
import java.util.function.Consumer;

public class BasherTester {
    public static void main(String...args) {
        System.out.printf("Creating Basher client\n");
        Basher.<String>of()
            .onMessage( printMessage )
            .onOpen( printConfig )
            .onError( printError )
            .fetch();
        System.out.printf("Waiting for data...\n");
    }
    
    private static final Consumer<String>              printMessage = System.out::println;
    private static final Consumer<Map<String, Object>> printConfig  = System.out::println;
    private static final Consumer<String>              printError   = System.out::println;
}
