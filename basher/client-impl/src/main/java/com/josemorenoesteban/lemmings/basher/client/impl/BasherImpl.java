package com.josemorenoesteban.lemmings.basher.client.impl;

import static java.lang.String.format;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;
import static com.netflix.hystrix.HystrixCommandGroupKey.Factory.asKey;

import com.josemorenoesteban.lemmings.basher.client.v1.Basher;
import com.netflix.hystrix.HystrixCommand;

import org.glassfish.tyrus.ext.client.java8.SessionBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

public final class BasherImpl implements Basher<String> {
    private static final String           VERSION            = "1.0.0-SNAPSHOT";
    private static final String           SERVICE_URL        = "ws://localhost:9090/v1/basher";
    private static final String           HYSTRIX_GROUP_NAME = "LemmingsGroup";
    private static final Supplier<String> DEFAULT_FALLBACK   = () -> "";

    private final URI serviceUrl;
    
    private Supplier<String>              fallback;
    private Consumer<String>              onMessage;
    private Consumer<Map<String, Object>> onOpen;
    private Consumer<Integer>             onClose;
    private Consumer<Throwable>           onError;
    
    public BasherImpl() {
        try {
            this.serviceUrl = new URI(SERVICE_URL);  // TODO change to get URL from environment variable, like requireNonNull(System.getenv(SERVICE_URL_ENV), SERVICE_URL_ENV + " environment variable not defined");
        } catch(URISyntaxException e) {
            getLogger(BasherImpl.class.getName()).log(INFO, () -> format("Error setting service URI %s", SERVICE_URL) );
            throw new RuntimeException(format("Error setting URI %s for client", SERVICE_URL), e);
        }
    }
    
    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public Basher fallback(final Supplier<String> fallback) {
        this.fallback = fallback;
        return this;
    }

    @Override
    public Basher onMessage(final Consumer<String> onMessage) {
        this.onMessage = onMessage;
        return this;
    }

    @Override
    public Basher onOpen(final Consumer<Map<String, Object>> onOpen) {
        this.onOpen = onOpen;
        return this;
    }
    
    @Override
    public Basher onClose(final Consumer<Integer> onClose) {
        this.onClose = onClose;
        return this;
    }

    @Override
    public Basher onError(final Consumer<Throwable> onError) {
        this.onError = onError;
        return this;
    }

    @Override
    public void fetch() {
        Command command = new Command();
        command.execute();
    }

    void onMessageHanlder(final String message) {
        if (onMessage != null) onMessage.accept(message);
    }
    
    void onOpenHanlder(final Session session, final EndpointConfig config) {
        if (onOpen != null) onOpen.accept( config.getUserProperties() ); 
    }
    
    void onErrorHanlder(final Session session, final Throwable error) {
        System.out.println("Un error!!!");
        if (onError != null) onError.accept(error);
    }
    
    void onCloseHanlder(final Session session, final CloseReason reason) {
        if (onClose != null) onClose.accept( reason.getCloseCode().getCode() );
    }

    private class Command  extends HystrixCommand<Void> {
        private Command() {
            super( asKey(HYSTRIX_GROUP_NAME) );
        }

        @Override
        protected Void run() throws Exception {
            SessionBuilder builder = new SessionBuilder()
                .uri( serviceUrl )
                .messageHandler( String.class, message ->  onMessageHanlder(message) ) 
                .onOpen( (session, config)  -> onOpenHanlder(session, config) )
                .onError( (session, error)  -> onErrorHanlder(session, error) )
                .onClose( (session, reason) -> onCloseHanlder(session, reason) );
            builder.connectAsync( newCachedThreadPool() );
            return null;
        }
    }
}
