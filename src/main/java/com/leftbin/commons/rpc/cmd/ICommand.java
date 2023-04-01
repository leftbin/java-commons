package com.leftbin.commons.rpc.cmd;


import io.grpc.stub.StreamObserver;
import lombok.Builder;
import lombok.Data;

// CI(command input) is the input object type for validation
// CO(command output) is the type of object that is produced as result of execution i.e the new state resulted after execution
public interface ICommand<I, O> {
    default CmdAuthorizationResult authorize(I input) {
        return CmdAuthorizationResult.builder().isAuthorizationVerified(true).isAuthorized(true).build();
    }

    default CmdValidationResult validate(I input) {
        return CmdValidationResult.builder().isValidated(true).isValid(true).build();
    }

    default CmdExecResult<O> execute(I input) {
        return CmdExecResult.<O>builder().isExecuted(true).build();
    }

    default CmdExecResult<O> execute(I input, StreamObserver<O> responseObserver) {
        return CmdExecResult.<O>builder().build();
    }

    @Data
    @Builder
    class CommandError {
        String code;
        String technicalDescription;
        String userDescription;
    }

    @Data
    @Builder
    class CmdAuthorizationResult {
        private boolean isAuthorizationVerified;
        private CommandError error;
        private boolean isAuthorized;
        private String unauthorizedReason;
    }

    @Data
    @Builder
    class CmdValidationResult {
        private boolean isValidated;
        private CommandError error;
        private boolean isValid;
    }

    @Data
    @Builder
    class CmdExecResult<O> {
        private boolean isExecuted;
        private CommandError error;
        private O output;
    }
}
