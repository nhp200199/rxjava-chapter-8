package com.tehmou.book.androidtictactoe.pojo;

public class SavedGame {
    GameState gameState;
    long timestamp;

    public SavedGame(GameState gameState, long timestamp) {
        this.gameState = gameState;
        this.timestamp = timestamp;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
