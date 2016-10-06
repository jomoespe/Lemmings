package com.josemorenoesteban.lemmings.app;

import com.josemorenoesteban.lemmings.climber.client.v1.Climber;

public class Main {
    static final Climber climber = Climber.of();
    
    public static void main(String...args) {
        System.out.printf("Service spec: %s, version: %s\n", Climber.getSpec(), climber.getVersion());

        climber
            .question("t")
            .start(2)
            .size(212)
            .get()
            .forEach( System.out::println );
    }
}
