package it.cnr.iit.utility.errorhandling.exception;

/**
 * Base exception class for all "unexpected" exceptions. Can add additional exception information,
 * if available.
 */
public class SystemException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final ErrorType errorType;

    public SystemException( ErrorType errorType ) {
        this.errorType = errorType;
    }

    public SystemException( ErrorType errorType, String message ) {
        super( message );
        this.errorType = errorType;
    }

    public SystemException( ErrorType errorType, String message, Throwable cause ) {
        super( message, cause );
        this.errorType = errorType;
    }

    public SystemException( ErrorType errorType, Throwable cause ) {
        super( cause );
        this.errorType = errorType;
    }

    public SystemException( String message ) {
        this( ErrorType.SYSTEM_ERROR, message );
    }

    public SystemException( String message, Throwable cause ) {
        this( ErrorType.SYSTEM_ERROR, message, cause );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( errorType );
        builder.append( ": " );
        builder.append( super.toString() );
        return builder.toString();
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
