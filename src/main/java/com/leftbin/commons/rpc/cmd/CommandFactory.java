package com.leftbin.commons.rpc.cmd;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CommandFactory {

    private Map<String, ICommand> commandMap = new HashMap<>();

    public CommandFactory(ApplicationContext applicationContext) {
        applicationContext.getBeansOfType(ICommand.class)
                .forEach((s, iCommand) -> commandMap.put(iCommand.getClass().getName(), iCommand));
    }

    public ICommand get(Class commandClass) {
        return commandMap.get(commandClass.getName());
    }
}
