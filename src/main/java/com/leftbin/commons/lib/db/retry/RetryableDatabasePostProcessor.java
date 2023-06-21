package com.leftbin.commons.lib.db.retry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * A Custom Bean processor for {@link RetryableDataSource datasource}
 * @author aroussi
 * @version %I% %G%
 * found this from this project https://github.com/roussi/spring-boot-retryable-db-connection.git
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RetryableDatabasePostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        if(bean instanceof DataSource dataSource) {
            log.info("-----> configuring a retryable datasource for beanName = {}", beanName);
            return new RetryableDataSource(dataSource);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        return bean;
    }
}
