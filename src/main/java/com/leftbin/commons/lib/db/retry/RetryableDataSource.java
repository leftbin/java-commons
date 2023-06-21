package com.leftbin.commons.lib.db.retry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This is a Decorator that add the retry capabilities to a {@link DataSource datasource}
 *
 * @author aroussi
 * @version %I% %G%
 * found this from this project https://github.com/roussi/spring-boot-retryable-db-connection.git
 */
@Slf4j
@RequiredArgsConstructor
public class RetryableDataSource extends AbstractDataSource {

    private final DataSource dataSource;

    // * maxAttempts = 100 is the max number of reties attempted to get database connection
    // * multiplier = 1.3 is the proportion of delay that is going to be increase to that of previous attempt.
    //   for example previous attempt delay is 1 sec then delay before current attempt is 1.3
    // * maxDelay = 10000 is the max delay allowed between two attempts
    @Override
    @Retryable(maxAttempts = 100, backoff = @Backoff(multiplier = 1.3, maxDelay = 10000))
    public Connection getConnection() throws SQLException {
        log.info("getting connection ...");
        return dataSource.getConnection();
    }

    @Override
    @Retryable(maxAttempts = 100, backoff = @Backoff(multiplier = 1.3, maxDelay = 10000))
    public Connection getConnection(String username, String password) throws SQLException {
        log.info("getting connection by username and password ...");
        return dataSource.getConnection(username, password);
    }
}
