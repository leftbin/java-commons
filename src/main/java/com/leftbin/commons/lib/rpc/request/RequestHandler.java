package com.leftbin.commons.lib.rpc.request;

import com.google.protobuf.Message;
import com.leftbin.commons.lib.rpc.request.annotation.RequestAnnotationVerifier;
import com.leftbin.commons.lib.rpc.request.exception.RequestInvalidException;
import com.leftbin.commons.lib.rpc.request.operation.StateCreateOperation;
import com.leftbin.commons.lib.rpc.request.operation.StateReadOperation;
import com.leftbin.commons.lib.rpc.request.operation.StateUpdateOperation;
import com.leftbin.commons.lib.rpc.request.streaming.StreamingRequest;
import com.leftbin.commons.lib.rpc.request.unary.UnaryRequest;
import com.leftbin.commons.lib.step.StepResult;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class RequestHandler<I extends Message, O extends Message> extends RequestHandlerBase {
    private final Request<I, O> request;
    private final io.grpc.Context.Key<Message> TARGET_RESOURCE_KEY = io.grpc.Context.key("TARGET_RESOURCE_KEY");

    public RequestHandler(Request<I, O> request) {
        this.request = request;
        super.stepResult = StepResult.newBuilder().build();
        super.context = new Context();
    }

    public void handle(I input, StreamObserver<O> responseObserver) {
        try {
            request.loadTarget(this, input);
        } catch (Exception e) {
            var message = String.format("failed to look up resource with error %s", e.getMessage());
            log.error(message);
            e.printStackTrace();
            responseObserver.onError(Status.INTERNAL.withDescription(message).asRuntimeException());
            return;
        }

        // for create command operation check for already exists
        if (Boolean.TRUE.equals(RequestAnnotationVerifier.verify(request, StateCreateOperation.class))) {
            if (!Objects.isNull(super.context.get(TARGET_RESOURCE_KEY))) {
                responseObserver.onError(Status.ALREADY_EXISTS.withDescription("already exists").asRuntimeException());
            }
        }

        // for delete,restore and update check for not found
        if (Boolean.TRUE.equals(RequestAnnotationVerifier.verify(request, StateReadOperation.class)) ||
            Boolean.TRUE.equals(RequestAnnotationVerifier.verify(request, StateUpdateOperation.class))
        ) {
            if (Objects.isNull(super.context.get(TARGET_RESOURCE_KEY))) {
                responseObserver.onError(Status.NOT_FOUND.withDescription("not found").asRuntimeException());
                return;
            }
        }

        try {
            try {
                // Load context required to execute the request
                request.loadContext(this, input);
            } catch (RequestInvalidException e) {
                responseObserver.onError(Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());
                return;
            } catch (Exception e) {
                e.printStackTrace();
                log.error("failed validating request. err: {}", e.getMessage());
                responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
                return;
            }

            // Verify context loading and handle failure
            if (!isContextLoaded()) {
                log.error("err loading context. err: {}", getContextLoadFailedReason());
                responseObserver.onError(Status.FAILED_PRECONDITION.withDescription(
                    getContextLoadFailedReason()).asRuntimeException());
                return;
            }

            try {
                // Validate the input request
                request.validate(this, input);
            } catch (RequestInvalidException e) {
                responseObserver.onError(Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());
                return;
            } catch (Exception e) {
                e.printStackTrace();
                log.error("failed validating request. err: {}", e.getMessage());
                responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
                return;
            }

            // Verify validation and handle failure
            if (!isValidated()) {
                log.error("err validating cmd. err: {}", getValidationFailedReason());
                responseObserver.onError(Status.INTERNAL.withDescription(
                    getValidationFailedReason()).asRuntimeException());
                return;
            }

            // Check if request is valid and handle failure
            if (!isValid()) {
                responseObserver.onError(Status.FAILED_PRECONDITION.withDescription(
                    getInvalidReason()).asRuntimeException());
                return;
            }

            try {
                if (request instanceof UnaryRequest) {
                    O output = (O) ((UnaryRequest<I, O>) request).execute(this, input);
                    responseObserver.onNext(output);
                } else if (request instanceof StreamingRequest) {
                    ((StreamingRequest<I, O>) request).execute(this, input, responseObserver);
                }
            } catch (RequestInvalidException e) {
                responseObserver.onError(Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());
                return;
            } catch (Exception e) {
                e.printStackTrace();
                log.error("failed executing request. err: {}", e.getMessage());
                responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
                return;
            }

            // Verify execution and handle failure
            if (!isExecuted()) {
                log.error("err executing cmd. err: {}", getExecutionFailedReason());
                responseObserver.onError(Status.INTERNAL.withDescription(
                    getExecutionFailedReason()).asRuntimeException());
                return;
            }

            // Signal the end of response stream
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("failed executing request. err: {}", e.getMessage());
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    //    https://chat.openai.com/share/002bf1e5-32b4-409b-9810-deb880faaac4
    private boolean isSameTypes(I input, Message targetResource) {
        return targetResource != null && input != null && targetResource.getClass().equals(input.getClass());
    }

    public void setTarget(Message target) {
        context.add(TARGET_RESOURCE_KEY, target);
    }

    public Message getTarget() {
        return context.get(TARGET_RESOURCE_KEY);
    }
}
