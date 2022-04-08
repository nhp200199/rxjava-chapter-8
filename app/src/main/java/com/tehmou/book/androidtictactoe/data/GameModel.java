package com.tehmou.book.androidtictactoe.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.tehmou.book.androidtictactoe.pojo.GameGrid;
import com.tehmou.book.androidtictactoe.pojo.GameState;
import com.tehmou.book.androidtictactoe.pojo.GameSymbol;
import com.tehmou.book.androidtictactoe.pojo.SavedGame;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class GameModel {
    public static final int GRID_WIDTH = 7;
    public static final int GRID_HEIGHT = 7;

    private BehaviorSubject<GameState> activeGameState;
    private PersistedGameStore gameStore;

    public GameModel(Context context) {
        GameGrid emptyGameGrid = new GameGrid(GRID_WIDTH, GRID_HEIGHT);
        GameState emptyGameState = new GameState(emptyGameGrid, GameSymbol.EMPTY);
        activeGameState = BehaviorSubject.createDefault(emptyGameState);

        SharedPreferences sharedPreferences = context.getSharedPreferences("games", Context.MODE_PRIVATE);
        gameStore = new PersistedGameStore(sharedPreferences);
    }

    public void newGame() {
        GameGrid emptyGameGrid = new GameGrid(GRID_WIDTH, GRID_HEIGHT);
        GameState emptyGameState = new GameState(emptyGameGrid, GameSymbol.EMPTY);
        activeGameState.onNext(emptyGameState);
    }
    public void putActiveGameState(GameState value) {
        activeGameState.onNext(value);
    }
    public Observable<GameState> getActiveGameState() {
        return activeGameState.hide();
    }

    public Observable<Void> saveActiveGame() {
        return gameStore.put(activeGameState.getValue());
    }

    public Observable<List<SavedGame>> getSavedGamesStream() {
        return gameStore.getSavedGamesStream();
    }

}
