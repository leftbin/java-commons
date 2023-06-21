package com.leftbin.commons.lib.rpc.request.method;

public class RequestMethod {
    public enum Type {
        UNARY,
        SERVER_STREAMING
    }

    public static final class Stack {
        public static final String preview = "preview";
        public static final String apply = "apply";
    }

    public static final class Command {
        public static final String add = "add";
        public static final String addMultiple = "addMultiple";
        public static final String clone = "clone";
        public static final String create = "create";
        public static final String delete = "delete";
        public static final String pause = "pause";
        public static final String restart = "restart";
        public static final String restore = "restore";
        public static final String unpause = "unpause";
        public static final String update = "update";
    }

    public static final class Query {
        public static final String findByCompanyId = "findByCompanyId";
        public static final String findByHostingClusterId = "findByHostingClusterId";
        public static final String findByProductEnvId = "findByProductEnvId";
        public static final String findByProductId = "findByProductId";
        public static final String findPods = "findPods";
        public static final String getById = "getById";
        public static final String getLogStream = "getLogStream";
        public static final String getPassword = "getPassword";
        public static final String list = "list";
        public static final String listByFilters = "listByFilters";
    }
}
