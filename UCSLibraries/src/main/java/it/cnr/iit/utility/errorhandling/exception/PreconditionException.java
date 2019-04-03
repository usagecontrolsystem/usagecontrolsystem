package it.cnr.iit.utility.errorhandling.exception;

/**
 * Throw this exception when a precondition in a method call does not hold.
 */
public class PreconditionException extends SystemException {
    private static final long serialVersionUID = -6204840303149650090L;

    public PreconditionException() {
        super( ErrorType.SYSTEM_ERROR );
    }

    public PreconditionException( String message ) {
        super( ErrorType.SYSTEM_ERROR, message );
    }

    public PreconditionException( String message, Throwable cause ) {
        super( ErrorType.SYSTEM_ERROR, message, cause );
    }

    public PreconditionException( Throwable cause ) {
        super( ErrorType.SYSTEM_ERROR, cause );
    }
}
