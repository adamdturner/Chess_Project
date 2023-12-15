package chess;

import com.google.gson.*;
import java.lang.reflect.Type;





public class ChessGameTypeAdapter implements JsonDeserializer<ChessGame> {
    @Override
    public ChessGame deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return context.deserialize(json, MyGame.class);
    }
}
