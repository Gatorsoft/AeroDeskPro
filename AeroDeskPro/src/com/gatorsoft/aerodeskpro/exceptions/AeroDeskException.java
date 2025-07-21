package com.gatorsoft.aerodeskpro.exceptions;

public class AeroDeskException extends Exception {

    private static final long serialVersionUID = 1L;

    // Error categories
    public enum ErrorCategory {
        DATABASE_ERROR,
        VALIDATION_ERROR,
        BUSINESS_RULE_VIOLATION,
        RESOURCE_NOT_FOUND,
        SYSTEM_ERROR,
        EXTERNAL_SERVICE_ERROR
    }

    private final ErrorCategory category;
    private final String errorCode;
    private final Object[] parameters;

    /**
     * Constructor with message and cause
     * @param message
     * @param cause
     */
    public AeroDeskException(String message, Throwable cause) {
        super(message, cause);
        this.category = ErrorCategory.SYSTEM_ERROR;
        this.errorCode = "SYSTEM_ERROR";
        this.parameters = null;
    }

    /**
     * Constructor with message, cause and category
     * @param message
     * @param cause
     * @param category
     */
    public AeroDeskException(String message, Throwable cause, ErrorCategory category) {
        super(message, cause);
        this.category = category;
        this.errorCode = category.name();
        this.parameters = null;
    }

    /**
     * Constructor with message, cause, category and error code
     * @param message
     * @param cause
     * @param category
     * @param errorCode
     */
    public AeroDeskException(String message, Throwable cause, ErrorCategory category, String errorCode) {
        super(message, cause);
        this.category = category;
        this.errorCode = errorCode;
        this.parameters = null;
    }

    /**
     * Constructor with message, cause, category, error code and parameters
     * @param message
     * @param cause
     * @param category
     * @param errorCode
     * @param parameters
     */
    public AeroDeskException(String message, Throwable cause, ErrorCategory category,
            String errorCode, Object... parameters) {
        super(message, cause);
        this.category = category;
        this.errorCode = errorCode;
        this.parameters = parameters;
    }

    public AeroDeskException() {
        this("Unknown error", null, ErrorCategory.SYSTEM_ERROR, "SYSTEM_ERROR");
    }

    /**
     * Constructor with message and category
     * @param message
     * @param category
     */
    public AeroDeskException(String message, ErrorCategory category) {
        this(message, null, category);
    }

    public ErrorCategory getCategory() {
        return category;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getParameters() {
        return parameters;
    }

    /**
     * Returns a detailed error message including category and error code
     * @return 
     */
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(category).append("] ");
        sb.append("[").append(errorCode).append("] ");
        sb.append(getMessage());

        if (parameters != null && parameters.length > 0) {
            sb.append(" - Parameters: ");
            for (int i = 0; i < parameters.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(parameters[i]);
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return getDetailedMessage();
    }
}
