package com.leftbin.commons.lib.rpc.request;

import com.google.protobuf.Message;
import lombok.NoArgsConstructor;

/**
 * Abstract class for an RPC Request.
 * <p>
 * This class represents a Request with a specified input and output type. It provides
 * methods for authorizing, validating, and executing the request.
 *
 * @param <I> The input object type for validation.
 */
@NoArgsConstructor
public abstract class Request<I extends Message, O extends Message> {
    /**
     * Looks up the target entity based on the information provided in the request.
     * If target object is found, it is added to the requestHandler context.
     * <p>
     * This method is intended to be executed prior to the authorization process.
     * It searches for the target entity in the database or other storage
     * based on the criteria specified in the request input.
     * <p>
     *
     * @param requestInput The input data from the request used to look up the entity.
     */
    public void loadTarget(RequestHandler handler, I input) {
    }

    public void authorize(RequestHandler handler, I input) {
    }

    public void loadContext(RequestHandler handler, I input) {

    }

    public void validate(RequestHandler handler, I input) {
    }
}
