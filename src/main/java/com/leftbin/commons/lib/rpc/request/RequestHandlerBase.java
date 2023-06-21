package com.leftbin.commons.lib.rpc.request;

import com.leftbin.commons.lib.rpc.request.exception.RequestFailedException;
import com.leftbin.commons.lib.rpc.request.exception.RequestInvalidException;
import com.leftbin.commons.lib.step.StepResult;
import com.leftbin.commons.lib.strings.StringListStringifier;
import com.google.protobuf.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * Handler for managing request data, including steps results and the current context.
 */
@Slf4j
public class RequestHandlerBase {
    public Context context;

    @Getter
    @Setter
    protected StepResult stepResult;

    /**
     * A wrapper around a gRPC Context to manage keys and values.
     */
    public static class Context {
        protected io.grpc.Context requestContext;

        /**
         * Initializes the gRPC Context to the root context.
         */
        public Context() {
            this.requestContext = io.grpc.Context.ROOT;
        }

        /**
         * Adds a value to the context under a specified key.
         *
         * @param key   the key to store the value under
         * @param value the value to store in the context
         * @return the updated Context
         */
        public <T> Context add(io.grpc.Context.Key<T> key, T value) {
            requestContext = requestContext.withValue(key, value);
            return this;
        }

        /**
         * Retrieves a value from the context under a specified key.
         *
         * @param key the key of the value to retrieve
         * @return the value stored under the specified key, or null if no value is found
         */
        public <T> T get(io.grpc.Context.Key<T> key) {
            return key.get(requestContext);
        }
    }

    public Boolean isAuthorized() {
        return stepResult.getErrors().size() == 0;
    }

    public String getUnauthorizedReason() {
        return getErrorsString();
    }

    public Boolean isValid() {
        return stepResult.getErrors().size() == 0 && stepResult.getExceptions().size() == 0;
    }

    public String getInvalidReason() {
        return getErrorsString();
    }

    public Boolean isContextLoaded() {
        return stepResult.getErrors().size() == 0 && stepResult.getExceptions().size() == 0;
    }

    public String getContextLoadFailedReason() {
        return convertErrorsAndExceptionsToString();
    }

    public Boolean isExecuted() {
        return stepResult.getErrors().size() == 0 && stepResult.getExceptions().size() == 0;
    }

    public String getExecutionFailedReason() {
        return getExceptionsString();
    }

    public void addStepResult(StepResult stepResult) {
        this.stepResult = this.stepResult.merge(stepResult);
    }

    public void addError(String message) {
        stepResult = stepResult.toBuilder().error(message).build();
    }

    public void addErrors(Collection<String> errors) {
        stepResult = stepResult.toBuilder().errors(errors).build();
    }

    public void addException(String message) {
        stepResult = stepResult.toBuilder().exception(message).build();
    }

    public void addExceptions(Collection<String> exceptions) {
        stepResult = stepResult.toBuilder().exceptions(exceptions).build();
    }

    public String getErrorsString() {
        return StringListStringifier.stringify(stepResult.getErrors());
    }

    public String getExceptionsString() {
        return StringListStringifier.stringify(stepResult.getExceptions());
    }

    public String convertErrorsAndExceptionsToString() {
        // Create a new list that includes both errors and exceptions
        var allIssues = new ArrayList<String>();
        allIssues.addAll(stepResult.getErrors());
        allIssues.addAll(stepResult.getExceptions());

        // Convert the list of all issues to a string
        return StringListStringifier.stringify(allIssues);
    }

    public Boolean isAuthorizationVerified() {
        return stepResult.getExceptions().size() == 0;
    }

    public String getAuthorizationVerificationFailedReason() {
        return getExceptionsString();
    }

    public Boolean isValidated() {
        return stepResult.getExceptions().size() == 0;
    }

    public String getValidationFailedReason() {
        return getExceptionsString();
    }

    //can only be used in validate and authorize methods.
    public <T> void stopOnPresent(Optional<T> optional, String message) {
        if (optional.isPresent()) {
            throw new RequestInvalidException(message);
        }
    }

    //can only be used in validate and authorize methods.
    public <T> void stopOnEmpty(Optional<T> optional, String message) {
        if (optional.isEmpty()) {
            throw new RequestInvalidException(message);
        }
    }

    //can only be used in validate and authorize methods.
    public <T> T stopOrGet(Optional<T> optional, String message) {
        if (optional.isEmpty()) {
            throw new RequestInvalidException(message);
        }
        return optional.get();
    }

    /**
     * Immediately stops the execution if the current request is invalid.
     * <p>
     * This method is a guard clause used in validation steps. If at any point of validation,
     * the request becomes invalid, this method should be called. It checks the validity
     * of the current request by calling {@link #isValid()}. If the request is not valid,
     * it raises a {@link RequestInvalidException} with the provided error message.
     * <p>
     * Use this method immediately after a validation step in the request handling process
     * to ensure the request doesn't proceed further in case of an invalid state.
     *
     * <p>
     * Example usage in validation process:
     *
     * <pre>
     * {@code
     * handler.addStepResult(CompanyIdValidator.validate(company));
     * handler.stopIfInvalid("Company id validation failed");
     *
     * handler.addStepResult(CompanyRequiredFieldsValidator.validate(company));
     * handler.stopIfInvalid("Required fields validation failed");
     *
     * companyRepo.findById(company.getMetadata().getId()).ifPresent(
     *     (companyEntity) -> {
     *         handler.addError("Company already exists");
     *         handler.stopIfInvalid("Company already exists");
     *     }
     * );
     * }
     * </pre>
     *
     * @param message The error message to be associated with the thrown
     *                {@link RequestInvalidException} if the request is invalid.
     * @throws RequestInvalidException if the current request is invalid.
     *                                 The exception includes the provided error message.
     */
    public void stopIfInvalid(String message) {
        if (isValid()) {
            return;
        }
        message = String.format("%s - %s", message, convertErrorsAndExceptionsToString());
        throw new RequestInvalidException(message);
    }

    public void abortIfInvalid(StepResult stepResult, String message) {
        this.addStepResult(stepResult);
        if (isValid()) {
            return;
        }
        message = String.format("%s - %s", message, convertErrorsAndExceptionsToString());
        throw new RequestFailedException(message);
    }

    public void stopWithError(String message) {
        throw new RequestInvalidException(message);
    }

    public void stopWithException(String message) {
        throw new RequestFailedException(message);
    }

    public void addExceptionOnFalse(Boolean condition, String message) {
        if (!Boolean.FALSE.equals(condition)) {
            return;
        }
        this.stepResult = this.stepResult.toBuilder().exception(message).build();
    }

    public void addErrorOnFalse(Boolean condition, String message) {
        if (!Boolean.FALSE.equals(condition)) {
            return;
        }
        this.stepResult = this.stepResult.toBuilder().error(message).build();
    }

    public void addExceptionOnTrue(Boolean condition, String message) {
        if (!Boolean.TRUE.equals(condition)) {
            return;
        }
        this.stepResult = this.stepResult.toBuilder().exception(message).build();
    }

    public void addErrorOnTrue(Boolean condition, String message) {
        if (!Boolean.TRUE.equals(condition)) {
            return;
        }
        this.stepResult = this.stepResult.toBuilder().error(message).build();
    }
}
