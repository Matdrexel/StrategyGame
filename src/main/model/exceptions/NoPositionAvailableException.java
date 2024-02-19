package model.exceptions;

// An exception for where there is no position available for a warrior to be placed
public class NoPositionAvailableException extends CheckedGameException {

    public NoPositionAvailableException(String msg) {
        super(msg);
    }
}
