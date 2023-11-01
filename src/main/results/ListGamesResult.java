package results;

import models.Game;

import java.util.List;

public class ListGamesResult {
    private List<Game> games;

    public ListGamesResult(List<Game> games) {
        this.games = games;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }
}

