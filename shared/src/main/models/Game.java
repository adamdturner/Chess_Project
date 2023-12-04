package models;

import chess.ChessGame;

/**
 * simple record class used for carrying data
 */
public record Game(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    private static State state;
    public enum State {
        WHITE,
        BLACK,
        DRAW,
        UNDECIDED
    }

    public boolean isGameOver() {
        return state != State.UNDECIDED;
    }

    public void setState(State newState) {
        state = newState;
    }

    public State getState() {
        return state;
    }
}
