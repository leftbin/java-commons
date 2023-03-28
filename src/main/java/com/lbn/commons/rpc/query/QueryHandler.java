package com.lbn.commons.rpc.query;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryHandler<I,O> {

    private IQuery<I,O> query;
    public QueryHandler(IQuery<I,O> query) {
        this.query = query;
    }

    public void handle(I input, StreamObserver<O> responseObserver) {
        var authorizationResult = query.authorize(input);
        if (!authorizationResult.isAuthorizationVerified()) {
            log.error("err authorizing query. err: {}", authorizationResult.getError().getTechnicalDescription());
            responseObserver.onError(Status.INTERNAL.withDescription(authorizationResult.getError().getTechnicalDescription()).asRuntimeException());
            return;
        }
        if (!authorizationResult.isAuthorized()) {
            responseObserver.onError(Status.PERMISSION_DENIED.withDescription(authorizationResult.getUnauthorizedReason()).asRuntimeException());
            return;
        }
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
        var execResult = query.execute(input);
        if (!execResult.isExecuted()) {
            log.error("err executing query. err: {}", execResult.getError().getTechnicalDescription());
            responseObserver.onError(Status.INTERNAL.withDescription(execResult.getError().getTechnicalDescription()).asRuntimeException());
            return;
        }
        responseObserver.onNext(execResult.getOutput());
        responseObserver.onCompleted();
    }
}
