package model.exceptions;

// An exception for when a warrior tries to move to a position already occupied by another warrior
public class OccupiedException extends CheckedGameException {

    public OccupiedException() {
        super("This position is already occupied");
    }
}
