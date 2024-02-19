package model.exceptions;

// The superclass for exceptions relating to warriors initializing/changing their stage positions
public class CheckedGameException extends Exception {

    public CheckedGameException(String msg) {
        super(msg);
    }
}
