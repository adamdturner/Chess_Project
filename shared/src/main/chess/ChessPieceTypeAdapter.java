package chess;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ChessPieceTypeAdapter implements JsonDeserializer<ChessPiece> {
    @Override
    public ChessPiece deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json.isJsonNull()) {
            return null; // Representing an empty square
        }

        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement pieceTypeElement = jsonObject.get("pieceType");
        JsonElement teamColorElement = jsonObject.get("teamColor");

        if (pieceTypeElement == null || teamColorElement == null) {
            throw new JsonParseException("Missing pieceType or teamColor in JSON");
        }

        String pieceType = pieceTypeElement.getAsString();
        ChessGame.TeamColor teamColor = ChessGame.TeamColor.valueOf(teamColorElement.getAsString());

        // Instantiate the correct subclass based on the piece type
        switch (pieceType) {
            case "PAWN":
                return new MyPawn(teamColor);
            case "ROOK":
                return new MyRook(teamColor);
            case "KNIGHT":
                return new MyKnight(teamColor);
            case "BISHOP":
                return new MyBishop(teamColor);
            case "QUEEN":
                return new MyQueen(teamColor);
            case "KING":
                return new MyKing(teamColor);
            default:
                throw new JsonParseException("Unknown piece type: " + pieceType);
        }
    }
}

