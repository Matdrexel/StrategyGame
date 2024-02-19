package model.exceptions;

// An exception for when a warrior attempts to move to a position farther than their movement stat allows
public class TooFarException extends CheckedGameException {

    public TooFarException() {
        super("This position is too far");
    }
}
