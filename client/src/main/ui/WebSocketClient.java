package ui;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.AuthToken;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class WebSocketClient extends Endpoint {

    private final Session session;
    private final ChessClient client;
    private AuthToken authToken;

    public WebSocketClient(String url, ChessClient client) {
        try {
            this.client = client;
            URI uri = new URI(url);

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    // Deserialize and handle message
                    ServerMessage serverMessage = deserializeFromJson(message, ServerMessage.class);
                    if (serverMessage != null) {
                        switch (serverMessage.getServerMessageType()) {
                            case LOAD_GAME:
                                // Handle game loading
                                LoadGameMessage loadGameMessage = deserializeFromJson(message, LoadGameMessage.class);
                                if (loadGameMessage != null) {
                                    client.load(loadGameMessage.getGame());
                                    System.out.println("LOAD_GAME message received");
                                }
                                break;
                            case ERROR:
                                // Handle error message by further deserializing as ErrorMessage
                                ErrorMessage errorMessage = deserializeFromJson(message, ErrorMessage.class);
                                if (errorMessage != null) {
                                    System.out.println(errorMessage.getErrorMessage());
                                    client.onServerMessage("Error from server: " + errorMessage.getErrorMessage());
                                }
                                break;
                            case NOTIFICATION:
                                // Handle notifications
                                NotificationMessage notificationMessage = deserializeFromJson(message, NotificationMessage.class);
                                if (notificationMessage != null) {
                                    System.out.println(notificationMessage.getMessage());
                                    client.onServerMessage("Error from server: " + notificationMessage.getMessage());
                                }
                                break;
                        }
                    }
                }

                private <T> T deserializeFromJson(String json, Class<T> clazz) {
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(ChessGame.class, new ChessGameTypeAdapter())
                            .registerTypeAdapter(ChessBoard.class, new ChessBoardTypeAdapter())
                            .registerTypeAdapter(ChessPiece.class, new ChessPieceTypeAdapter())
                            .create();
                    return gson.fromJson(json, clazz);
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {}

    private String serializeToJson(Object object) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ChessGame.class, new ChessGameTypeAdapter())
                .registerTypeAdapter(ChessBoard.class, new ChessBoardTypeAdapter())
                .registerTypeAdapter(ChessPiece.class, new ChessPieceTypeAdapter())
                .create();
        return gson.toJson(object);
    }

    public void sendCommand(Object object) {
        try {
            this.session.getBasicRemote().sendText(serializeToJson(object));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
