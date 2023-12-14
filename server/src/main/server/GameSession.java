package server;

import models.Game;
import models.User;

import org.eclipse.jetty.websocket.api.Session;

public class GameSession {
    private final Session session;
    private final User user;
    private Game game;

    public GameSession(Session session, User user) {
        this.session = session;
        this.user = user;
    }

    public Session getSession() {
        return session;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public User getUser() {
        return user;
    }

    // Override equals and hashCode if necessary, especially if you're storing these in a Set or using them as keys in a Map
}

