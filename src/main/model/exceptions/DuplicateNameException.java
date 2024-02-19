package model.exceptions;

public class DuplicateNameException extends CheckedGameException {

    public DuplicateNameException() {
        super("A duplicate name was added for an Army/Warrior");
    }
}
