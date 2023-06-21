package com.leftbin.commons.lib.step;

import com.leftbin.commons.lib.strings.StringListStringifier;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class StepResult {

    private List<String> errors;
    private List<String> exceptions;

    private StepResult(StepResultBuilder builder) {
        this.errors = builder.errors;
        this.exceptions = builder.exceptions;
    }

    public static StepResultBuilder newBuilder() {
        return new StepResultBuilder();
    }

    public StepResultBuilder toBuilder() {
        return new StepResultBuilder().errors(new ArrayList<>(this.errors))
            .exceptions(new ArrayList<>(this.exceptions));
    }

    /**
     * Transforms the error messages and exception message into a comma seperated string.
     * <p>
     * If there are no error or exception messages, an empty string is returned.
     *
     * @return The single comma seperated error message string
     */
    @Override
    public String toString() {
        var allErrors = new ArrayList<String>();
        allErrors.addAll(errors);
        allErrors.addAll(exceptions);
        return StringListStringifier.stringify(allErrors);
    }

    /**
     * Merges the current StepResult with another StepResult.
     * <p>
     * This method merges the errors and exceptions from the current and provided StepResult.
     * The merging logic depends on the implementation of the `errors` and `exceptions` methods
     * in the StepResult builder.
     * <p>
     * Note: This method does not modify the current or provided StepResult instances.
     *
     * @param that the other StepResult to merge with the current StepResult
     * @return a new StepResult containing the merged data
     * @throws NullPointerException if the provided StepResult is null
     */
    public StepResult merge(StepResult that) {
        return StepResult.newBuilder()
            .merge(this)
            .merge(that)
            .build();
    }

    public boolean isValid() {
        return this.errors.isEmpty() && this.exceptions.isEmpty();
    }

    public static class StepResultBuilder {
        private List<String> errors = new ArrayList<>();
        private List<String> exceptions = new ArrayList<>();

        public StepResultBuilder error(String error) {
            this.errors.add(error);
            return this;
        }

        public StepResultBuilder exception(String exception) {
            this.exceptions.add(exception);
            return this;
        }

        public StepResultBuilder errors(Collection<String> errors) {
            this.errors.addAll(errors);
            return this;
        }

        public StepResultBuilder exceptions(Collection<String> exceptions) {
            this.exceptions.addAll(exceptions);
            return this;
        }

        public StepResultBuilder clearErrors() {
            this.errors.clear();
            return this;
        }

        public StepResultBuilder clearExceptions() {
            this.exceptions.clear();
            return this;
        }

        /**
         * Merges the current StepResult with another StepResult.
         * <p>
         * This method merges the errors and exceptions from the current and provided StepResult.
         * The merging logic depends on the implementation of the `errors` and `exceptions` methods
         * in the StepResult builder.
         * <p>
         * Note: This method does not modify the current or provided StepResult instances.
         *
         * @param that the other StepResult to merge with the current StepResult
         * @return a new StepResult containing the merged data
         * @throws NullPointerException if the provided StepResult is null
         */
        public StepResultBuilder merge(StepResult that) {
            if (that != null) {
                this.errors.addAll(that.errors);
                this.exceptions.addAll(that.exceptions);
            }
            return this;
        }

        public StepResult build() {
            return new StepResult(this);
        }
    }
}
