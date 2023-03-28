package com.lbn.commons.rpc.query;


import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StreamingQueryHandler<I, O> {

    private IQuery<I,O> query;

    public StreamingQueryHandler(IQuery<I,O> query) {
        this.query = query;
    }

    public void handle(I input, StreamObserver<O> responseObserver) {

        var validationResult = query.validate(input);
        if (!validationResult.isValidated()) {
            log.error("err validating query. err: {}", validationResult.getError().getTechnicalDescription());
            responseObserver.onError(Status.INTERNAL.withDescription(validationResult.getError().getTechnicalDescription()).asRuntimeException());
            return;
        }
        if (!validationResult.isValid()) {
            responseObserver.onError(Status.FAILED_PRECONDITION.withDescription(validationResult.getError().getTechnicalDescription()).asRuntimeException());
            return;
        }
        var execResult = query.execute(input, responseObserver);
        if (!execResult.isExecuted()) {
            log.error("err executing query. err: {}", execResult.getError().getTechnicalDescription());
            responseObserver.onError(Status.INTERNAL.withDescription(execResult.getError().getTechnicalDescription()).asRuntimeException());
            return;
        }
        responseObserver.onCompleted();
    }
}
