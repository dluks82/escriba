package dev.dluks.escriba.domain.exceptions;

public abstract class EscribaException extends RuntimeException {

    protected EscribaException(String message) {
        super(message);
    }

    protected EscribaException(String message, Throwable cause) {
        super(message, cause);
    }

}
