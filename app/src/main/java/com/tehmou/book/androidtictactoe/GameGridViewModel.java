package com.tehmou.book.androidtictactoe;

import android.util.Pair;

import com.tehmou.book.androidtictactoe.pojo.GameGrid;
import com.tehmou.book.androidtictactoe.pojo.GameState;
import com.tehmou.book.androidtictactoe.pojo.GameSymbol;
import com.tehmou.book.androidtictactoe.pojo.GridPosition;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;

public class GameGridViewModel {

    private CompositeDisposable compositeDisposable;
    private BehaviorSubject<GameState> gameState;
    private Observable<GridPosition> gridPositionObservable;
    private Observable<GameSymbol> playerInTurnObservable;

    public GameGridViewModel(Observable<GridPosition> gridPositionObservable) {
        GameGrid emptyGameGrid = new GameGrid(3, 3);
        GameState emptyGameState = new GameState(emptyGameGrid, GameSymbol.EMPTY);
        gameState = BehaviorSubject.createDefault(emptyGameState);
        compositeDisposable = new CompositeDisposable();
        this.gridPositionObservable = gridPositionObservable;
        playerInTurnObservable = gameState.map(GameState::getLastPlayedSymbol)
        .map(lastSymbol -> {
            if (lastSymbol == GameSymbol.CIRCLE)
                return GameSymbol.CROSS;
            else
                return GameSymbol.CIRCLE;
        });
    }

    public void subscribe() {
        Observable<Pair<GameState, GameSymbol>>
                gameInfoObservable = Observable.combineLatest(
                gameState, playerInTurnObservable, Pair::new);

        compositeDisposable.add(
                gridPositionObservable
                        .withLatestFrom(
                                gameInfoObservable,
                                (gridPosition, gameInfo) ->
                                        gameInfo.first.setSymbolAt(
                                                gridPosition, gameInfo.second)
                        )
                        .subscribe(gameState::onNext)
        );
    }

    public void unsubscribe() {
        compositeDisposable.clear();
    }

    public Observable<GameGrid> getGameGridViewObservable() {
        return gameState.map(GameState::getGameGrid);
    }

    public Observable<GameSymbol> getPlayerInTurnObservable() {
        return playerInTurnObservable;
    }
}
