package com.leftbin.commons.lib.rpc.request.unary;

import com.leftbin.commons.lib.rpc.request.Request;
import com.leftbin.commons.lib.rpc.request.RequestHandler;
import com.google.protobuf.Message;

/**
 * Abstract class for a unary RPC Request.
 *
 * This class represents a unary Request with a specified input and output type. It extends the
 * Request class and overrides the execute method to provide unary execution.
 *
 * @param <I> The input object type for validation.
 * @param <O> The output object type produced as a result of execution.
 */
public abstract class UnaryRequest<I extends Message, O extends Message> extends Request<I,O> {
    public abstract O execute(RequestHandler handler, I input);
}
