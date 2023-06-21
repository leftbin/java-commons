package com.leftbin.commons.lib.rpc.request.method;

/**
 * The RpcMethodNameGetter class provides a utility method for fetching the name of the gRPC method
 * from the current thread's call stack that invoked it.
 * <p>
 * This class is typically used in the implementation of gRPC methods for retrieving the corresponding
 * handler from a map of handlers dynamically, thus facilitating a common handling mechanism for different
 * gRPC methods.
 * <p>
 * Note: The correctness of this mechanism depends on the structure of the application's call stack.
 * Changes to that structure might necessitate adjustments to the skipCount.
 */
class RequestMethodNameGetter {
    /**
     * The number of stack trace elements to skip when retrieving the method name from the call stack.
     * Adjust this value based on the specific structure of the call stack in your application.
     */
    private static final Integer skipCount = 1;

    /**
     * Retrieves the name of the gRPC method that invoked the current sequence of calls.
     * <p>
     * It navigates the call stack by skipping over a number of methods from the top of the stack trace
     * and then retrieves the name of the method that lies after these skipped methods.
     *
     * @return The name of the invoking gRPC method
     */
    public static String get() {
        return Thread.currentThread().getStackTrace()[1 + skipCount].getMethodName();
    }
}
