package models;

import chess.ChessGame;

/**
 * simple record class used for carrying data
 */
public record Game(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    private static GameState state;
    public enum GameState {
        WHITE,
        BLACK,
        DRAW,
        UNDECIDED
    }

    public Game setWhite(String whiteName) {
        return new Game(gameID, whiteName, blackUsername, gameName, game);
    }

    public Game setBlack(String blackName) {
        return new Game(gameID, whiteUsername, blackName, gameName, game);
    }

    public boolean isGameOver() {
        return state != GameState.UNDECIDED;
    }

    public void setState(GameState newState) {
        state = newState;
    }

    public GameState getState() {
        return state;
    }
}
