package com.josemorenoesteban.lemmings.climber.client.impl;

import com.josemorenoesteban.lemmings.climber.client.v1.Climber;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ClimberImpl implements Climber {
    public static final String VERSION         = "1.0.0-SNAPSHOT";
    public static final String SERVICE_URL_ENV = "SERVICE_URL";
    public static final String SERVICE_URL     = "http://localhost:4567/v1/climber";

    private final String serviceUrl;
    private final Client client;

    private Optional<String>  question = Optional.empty();
    private Optional<Integer> start    = Optional.empty();
    private Optional<Integer> size     = Optional.empty();
    
    public ClimberImpl() {
        this.serviceUrl = SERVICE_URL;  // TODO change to get URL from environment variable, like requireNonNull(System.getenv(SERVICE_URL_ENV), SERVICE_URL_ENV + " environment variable not defined");
        this.client     = ClientBuilder.newClient();
    }
    
    @Override
    public String getVersion() {
        return VERSION;
    }
    
    @Override
    public Climber question(final String question) {
        this.question = Optional.of(question);
        return this;
    }

    @Override
    public Climber start(final int start) {
        this.start = Optional.of(start);
        return this;
    }

    @Override
    public Climber size(final int size) {
        this.size = Optional.of(size);
        return this;
    }

    @Override
    public List<String> invoke() {
        List<String> ret = new ArrayList<>();
        Response response = client
                .target(SERVICE_URL)
                .queryParam("q",     question.orElse(""))
                .queryParam("start", start.orElse(0))
                .queryParam("size",  size.orElse(10))
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
        }
        return ret;
    }
}
