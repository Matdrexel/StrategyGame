package model.exceptions;

// Represents an exception used for when a warrior method is called when the warrior has no position
public class NoPositionException extends UncheckedGameException {

    public NoPositionException() {
        super("Warrior is attempting to do something requiring them to have a position.");
    }
}
