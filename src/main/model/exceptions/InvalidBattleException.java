package model.exceptions;

public class InvalidBattleException extends UncheckedGameException {

    public InvalidBattleException() {
        super("Battle started with warriors not placed properly on the stage");
    }
}
