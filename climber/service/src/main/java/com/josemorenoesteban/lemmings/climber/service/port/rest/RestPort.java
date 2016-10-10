package com.josemorenoesteban.lemmings.climber.service.port.rest;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.staticFiles;

import com.josemorenoesteban.lemmings.climber.service.domain.Data;

import spark.Request;

import java.util.function.Function;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

public class RestPort {
    private static final String GET_DATA_URI      = "/v1/climber";
    private static final String DOCUMENTATION_URI = "/v1/climber/swagger";
    private static final String HEALTH_CHECK_URI  = "/v1/climber/health";
    
    private static final String JSON_MIME         = "application/json";
    private static final String PROTOBUFF_MIME    = "application/protobuf";
    private static final String TEXT_MIME         = "text/plain";
  
    private final Documentation doc;
    private final HealthCheck   health;
    
    private final Function<Request, Query>               requestToQuery  = request -> new Query(request);
    private final Function<Stream<Data>, Stream<String>> getNames        = data    -> data.map( d -> d.name() );
    private final Function<Stream<String>, String>       toText          = content -> content.map( s -> s + " " ).collect( StringBuilder::new, StringBuilder::append, StringBuilder::append ).toString();
    private final Function<Stream<String>, JsonArray>    toJsonArray     = content -> content.collect( Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add ).build();
    private final Function<Stream<String>, byte[]>       toProtobuff     = content -> "esto deberia ser un protobuff".getBytes();
    
    public RestPort(final Function<Query, Stream<Data>> getData) {
        this.doc    = new Documentation();
        this.health = new HealthCheck();
        staticFiles.location("/web");
        before( (req, res) -> {} );
        after( (req, res) -> res.type(req.contentType()) );
        after( (req, res) -> res.header("Content-Encoding", "gzip") );
        get( DOCUMENTATION_URI, JSON_MIME,      doc::swagger );
        get( HEALTH_CHECK_URI,  JSON_MIME,      health::check );
        get( GET_DATA_URI,      TEXT_MIME,      (req, res) -> requestToQuery.andThen(getData).andThen(getNames).andThen(toText).apply(req) );
        get( GET_DATA_URI,      JSON_MIME,      (req, res) -> requestToQuery.andThen(getData).andThen(getNames).andThen(toJsonArray).apply(req) );
        get( GET_DATA_URI,      PROTOBUFF_MIME, (req, res) -> requestToQuery.andThen(getData).andThen(getNames).andThen(toProtobuff).apply(req) );
    }
}
