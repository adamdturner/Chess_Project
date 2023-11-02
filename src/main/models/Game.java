package models;

import chess.ChessGame;

/**
 * simple record class used for carrying data
 */
public record Game(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {}
