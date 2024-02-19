package model.exceptions;

// An exception for when a warrior than cannot move attempts to move across the stage
public class ImmobileException extends CheckedGameException {

    public ImmobileException() {
        super("You cannot move right now");
    }
}
