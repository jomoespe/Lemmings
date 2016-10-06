package com.josemorenoesteban.lemmings.climber.client.impl;

import static com.netflix.hystrix.HystrixCommandGroupKey.Factory.asKey;
import static java.util.Optional.ofNullable;

import com.josemorenoesteban.lemmings.climber.client.v1.Climber;
import com.netflix.hystrix.HystrixCommand;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import javax.json.Json;
import javax.json.JsonValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ClimberImpl implements Climber {
    private static final String VERSION            = "1.0.0-SNAPSHOT";
    private static final String SERVICE_URL_ENV    = "SERVICE_URL";
    private static final String SERVICE_URL        = "http://localhost:4567/v1/climber";
    private static final String HYSTRIX_GROUP_NAME = "LemmingsGroup";
    private static final Function<String, List<String>> DEFAULT_FALLBACK = s -> Collections.emptyList();

    private final String serviceUrl;
    private final Client client;

    private Function<String, List<String>> fallback;
    private String  question;
    private Integer start;
    private Integer size;
    
    public ClimberImpl() {
        this.serviceUrl = SERVICE_URL;  // TODO change to get URL from environment variable, like requireNonNull(System.getenv(SERVICE_URL_ENV), SERVICE_URL_ENV + " environment variable not defined");
        this.client     = ClientBuilder.newClient();
    }
    
    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public Climber fallback(final Function<String, List<String>> fallback) {
        this.fallback = fallback;
        return this;
    }
    
    @Override
    public Climber question(final String question) {
        this.question = question;
        return this;
    }

    @Override
    public Climber start(final int start) {
        this.start = start;
        return this;
    }

    @Override
    public Climber size(final int size) {
        this.size = size;
        return this;
    }

    @Override
    public List<String> invoke() {
        final InvokeCommand command = new InvokeCommand();
        return command.execute();
    }
    
    private class InvokeCommand  extends HystrixCommand<List<String>> {
        private InvokeCommand() {
            super(asKey(HYSTRIX_GROUP_NAME));
        }
        @Override
        protected List<String> run() throws Exception {
            List<String> ret = new ArrayList<>();
            Response response = client
                    .target(SERVICE_URL)
                    .queryParam("q",     ofNullable(question).orElse(""))
                    .queryParam("start", ofNullable(start).orElse(0))
                    .queryParam("size",  ofNullable(size).orElse(10))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();
            if (response.getStatusInfo().equals( Response.Status.OK )) {
                Json.createReader( (InputStream)response.getEntity() )
                    .readArray()
                    .stream()
                    .map( JsonValue::toString )
                    .forEach( ret::add );
            } else {
                System.err.printf("Error invoking service. Error code: %s, Reason: %s\n", response.getStatus(), response.getStatusInfo().getReasonPhrase());
                // TODO Thorw an exception???
            }
            return ret;
        }

        @Override
        protected List<String> getFallback() {
            return ofNullable(fallback).isPresent() 
                 ? fallback.apply( ofNullable(question).orElse("") ) 
                 : DEFAULT_FALLBACK.apply("");
        }    
    }
}
