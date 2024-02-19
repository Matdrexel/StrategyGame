package model.exceptions;

// An exception for when a warrior is placed on a non-existant position of the stage
public class InvalidPositionException extends CheckedGameException {

    public InvalidPositionException() {
        super("This position does not exist");
    }
}
