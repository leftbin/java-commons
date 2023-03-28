package com.lbn.commons.rpc.query;

import io.grpc.stub.StreamObserver;
import lombok.Builder;
import lombok.Data;

// QI(query input) is the input object type for validation
// QO(query output) is the type of object that is produced as result of execution i.e the new state resulted after execution
public interface IQuery<I, O> {

    default QueryAuthorizationResult authorize(I input) {
        return QueryAuthorizationResult.builder().isAuthorizationVerified(true).isAuthorized(true).build();
    }

    default QueryValidationResult validate(I input) {
        return QueryValidationResult.builder().isValidated(true).isValid(true).build();
    }

    default QueryExecResult<O> execute(I input) {
        return QueryExecResult.<O>builder().isExecuted(true).build();
    }

    default QueryExecResult<O> execute(I input, StreamObserver<O> responseObserver) {
        return QueryExecResult.<O>builder().build();
    }

    @Data
    @Builder
    class QueryError {
        String code;
        String technicalDescription;
        String userDescription;
    }

    @Data
    @Builder
    class QueryAuthorizationResult {
        private boolean isAuthorizationVerified;
        private QueryError error;
        private boolean isAuthorized;
        private String unauthorizedReason;
    }

    @Data
    @Builder
    class QueryValidationResult {
        private boolean isValidated;
        private QueryError error;
        private boolean isValid;
    }

    @Data
    @Builder
    class QueryExecResult<O> {
        private boolean isExecuted;
        private QueryError error;
        private O output;
    }
}
