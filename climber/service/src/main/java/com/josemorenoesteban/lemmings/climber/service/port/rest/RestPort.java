package com.josemorenoesteban.lemmings.climber.service.port.rest;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.get;

import com.josemorenoesteban.lemmings.climber.service.domain.Data;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import static java.sql.Timestamp.valueOf;
import java.util.List;
import java.util.function.BiFunction;

import spark.Request;

import java.util.function.Function;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

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
        before( (req, res) -> {} );
        after( (req, res) -> res.type(req.contentType()) );
        after( (req, res) -> res.header("Content-Encoding", "gzip") );
        get( DOCUMENTATION_URI, JSON_MIME,      doc::swagger );
        get( HEALTH_CHECK_URI,  JSON_MIME,      health::check );
        get( GET_DATA_URI,      TEXT_MIME,      (req, res) -> requestToQuery.andThen(getData).andThen(getNames).andThen(toText).apply(req) );
        get( GET_DATA_URI,      JSON_MIME,      (req, res) -> requestToQuery.andThen(getData).andThen(getNames).andThen(toJsonArray).apply(req) );
        get( GET_DATA_URI,      PROTOBUFF_MIME, (req, res) -> requestToQuery.andThen(getData).andThen(getNames).andThen(toProtobuff).apply(req) );
        
        
        get("/kkdkdt", (req, res) -> {
            try(Connection con = dataSource.getConnection()) {
                DAO<Contact> findZoomanContact = new DAO( () -> con, emailsBetweenDates, asContact);
                findZoomanContact
                    .execute(valueOf("2016-10-02 00:00:00"), valueOf("2016-10-07 23:59:59"))
                    .forEach(contact -> { 
                        try {
                            HttpServletResponse innerResponse = res.raw();
                            innerResponse.getWriter().println(contact);
                            innerResponse.flushBuffer();
                        } catch (IOException e) {
                            e.printStackTrace(System.err);
                        }
                    });
                
                return null;
            }
        } );
    }
    
    private static final String URL      = "jdbc:oracle:thin:@//dev-snap-03:1521/snapdb_dev.web.zooplus.de";
    private static final String USER     = "developer";
    private static final String PASSWORD = "z00dev";
    private static DataSource dataSource;
    
    static {
        BasicDataSource basicDs = new BasicDataSource();
        basicDs.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        basicDs.setUsername(USER);
        basicDs.setPassword(PASSWORD);
        basicDs.setUrl(URL);
        basicDs.setMaxActive(10);
        basicDs.setMaxIdle(5);
        basicDs.setInitialSize(5);
        basicDs.setValidationQuery("SELECT 1 from dual");
        dataSource = basicDs;
    }

    class Contact {
        private Long id;
        private String type;
        private String email;
        private String name;
        //private String text;
        private Timestamp date;
        private String subject;
        private String language;
        private Timestamp receivedDate;

        @Override
        public String toString() {
            return String.format("id:[%s], type:[%s], email:[%s], date:[%s], subject:[%s], language:[%s], received:[%s], ", 
                                 id, type, email, date, subject, language, receivedDate);
        }
    }
    private final BiFunction<Connection, List<Object>, PreparedStatement> emailsBetweenDates = (con, input) -> {
        final String EMAILS_BETWEEN_DATES = 
                "select CI_ID, CI_TYP, CI_EMAIL, CI_VNAME, CI_TEXT, CI_CDATUM, CI_BETREFF, CI_LANGUAGE, CI_RECEIVED_DATE " +
                "  from ZOOMAN.CONTACT_IN " +
                " where CI_CDATUM >= ? and CI_CDATUM <= ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(EMAILS_BETWEEN_DATES);
            int i = 1;
            for (Object value : input) {
                pstmt.setObject(i++, value);
            }
            return pstmt;
        } catch (SQLException e) {
            throw new RuntimeException("Error preparing the query", e);
        }
    };

    private final Function<ResultSet, Contact> asContact = (rs) -> {
        try {
            Contact contact = new Contact();
            contact.id           = rs.getLong("CI_ID");
            contact.type         = rs.getString("CI_TYP");
            contact.email        = rs.getString("CI_EMAIL");
            contact.name         = rs.getString("CI_VNAME");
            contact.date         = rs.getTimestamp("CI_CDATUM");
            contact.subject      = rs.getString("CI_BETREFF");
            contact.language     = rs.getString("CI_LANGUAGE");
            contact.receivedDate = rs.getTimestamp("CI_RECEIVED_DATE");
            return contact;
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping the resultset", e);
        }
    };
}
