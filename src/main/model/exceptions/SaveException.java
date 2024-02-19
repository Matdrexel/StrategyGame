package model.exceptions;

// Represents an exception for when the game does not save properly
public class SaveException extends CheckedGameException {

    public SaveException() {
        super("JSON data cannot be read properly");
    }
}
