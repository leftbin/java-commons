package com.lbn.commons.rpc.query;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class QueryFactory {

    private Map<String, IQuery> queryMap = new HashMap<>();

    public QueryFactory(ApplicationContext applicationContext) {
        applicationContext.getBeansOfType(IQuery.class)
                .forEach((s, iQuery) -> queryMap.put(iQuery.getClass().getName(), iQuery));
    }

    public IQuery get(Class queryClass) {
        return queryMap.get(queryClass.getName());
    }
}
