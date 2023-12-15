package chess;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class ChessMoveTypeAdapter implements JsonDeserializer<ChessMove> {
    @Override
    public ChessMove deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        // Assuming MyBoard is the concrete implementation of ChessBoard
        return context.deserialize(json, MyMove.class);
    }
}
