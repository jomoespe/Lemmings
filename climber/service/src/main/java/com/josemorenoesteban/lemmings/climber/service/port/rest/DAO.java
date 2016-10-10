package com.josemorenoesteban.lemmings.climber.service.port.rest;

import static java.lang.Long.MAX_VALUE;
import static java.util.Spliterator.ORDERED;
import static java.util.Arrays.asList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.Spliterators;
import java.util.function.Supplier;

public final class DAO<T> {
    private final Supplier<Connection>                                    connectionSupplier;
    private final BiFunction<Connection, List<Object>, PreparedStatement> query;
    private final Function<ResultSet, T>                                  mapResultSet;
    
    public DAO(final Supplier<Connection>                                    conSupplier,
                 final BiFunction<Connection, List<Object>, PreparedStatement> query,
                 final Function<ResultSet, T>                                  mapResultSet) {
        this.connectionSupplier = conSupplier;
        this.query              = query;
        this.mapResultSet       = mapResultSet;
    }

    public Stream<T> execute(final Object...inputs) throws RuntimeException {
        return StreamSupport.stream(new MySplitterator(connectionSupplier.get(), inputs), true);
    }

    private class MySplitterator extends Spliterators.AbstractSpliterator<T> {
        private final ResultSet resultset;
        
        private MySplitterator(final Connection con, final Object...inputs) {
            super(MAX_VALUE, ORDERED);
            try {
                resultset = query.apply(con, asList(inputs)).executeQuery();
            } catch(SQLException e ) {
                throw new RuntimeException("Error executing query", e);
            }
        }
        
        private void closeAll() throws SQLException {
            resultset.close();
        }
        
        @Override
        public boolean tryAdvance(Consumer<? super T> action) throws RuntimeException {
            try {
                if (!resultset.isClosed() && resultset.next()) {
                    action.accept( mapResultSet.apply( resultset ) );
                    return true;
                } else {
                    closeAll();
                    return false;
                }
            } catch(SQLException e) {
                throw new RuntimeException("Error mapping resultset", e);
            }
        }
    }
}
