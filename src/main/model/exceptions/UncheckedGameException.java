package model.exceptions;

// Represents exceptions that should not be thrown during the game
public class UncheckedGameException extends RuntimeException {

    public UncheckedGameException(String msg) {
        super(msg);
    }
}
