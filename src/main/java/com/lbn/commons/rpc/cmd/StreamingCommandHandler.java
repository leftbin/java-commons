package com.lbn.commons.rpc.cmd;


import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StreamingCommandHandler<I, O> {

    private ICommand<I, O> command;

    public StreamingCommandHandler(ICommand<I, O> command) {
        this.command = command;
    }

    public void handle(I input, StreamObserver<O> responseObserver) {
        var authorizationResult = command.authorize(input);
        if (!authorizationResult.isAuthorizationVerified()) {
            log.error("err authorizing cmd. err: {}", authorizationResult.getError().getTechnicalDescription());
            responseObserver.onError(Status.INTERNAL.withDescription(authorizationResult.getError().getTechnicalDescription()).asRuntimeException());
            return;
        }
        if (!authorizationResult.isAuthorized()) {
            responseObserver.onError(Status.PERMISSION_DENIED.withDescription(authorizationResult.getUnauthorizedReason()).asRuntimeException());
            return;
        }
        var validationResult = command.validate(input);
        if (!validationResult.isValidated()) {
            log.error("err validating cmd. err: {}", validationResult.getError().getTechnicalDescription());
            responseObserver.onError(Status.INTERNAL.withDescription(validationResult.getError().getTechnicalDescription()).asRuntimeException());
            return;
        }
        if (!validationResult.isValid()) {
            responseObserver.onError(Status.FAILED_PRECONDITION.withDescription(validationResult.getError().getTechnicalDescription()).asRuntimeException());
            return;
        }
        var execResult = command.execute(input, responseObserver);
        if (!execResult.isExecuted()) {
            log.error("err executing cmd. err: {}", execResult.getError().getTechnicalDescription());
            responseObserver.onError(Status.INTERNAL.withDescription(execResult.getError().getTechnicalDescription()).asRuntimeException());
            return;
        }
        responseObserver.onCompleted();
    }
}
