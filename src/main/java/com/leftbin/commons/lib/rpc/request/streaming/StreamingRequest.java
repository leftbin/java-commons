package com.leftbin.commons.lib.rpc.request.streaming;

import com.leftbin.commons.lib.rpc.request.Request;
import com.leftbin.commons.lib.rpc.request.RequestHandler;
import com.google.protobuf.Message;
import io.grpc.stub.StreamObserver;

public abstract class StreamingRequest<I extends Message, O extends Message> extends Request<I, O> {
    public abstract void execute(RequestHandler handler, I input, StreamObserver<O> responseObserver);
}
