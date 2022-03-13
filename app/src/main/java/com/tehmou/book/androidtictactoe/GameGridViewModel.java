package com.tehmou.book.androidtictactoe;

import android.util.Pair;

import com.tehmou.book.androidtictactoe.pojo.GameGrid;
import com.tehmou.book.androidtictactoe.pojo.GameState;
import com.tehmou.book.androidtictactoe.pojo.GameStatus;
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
    private Observable<GameStatus> gameStatusObservable;
    private Observable<String> winner;

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

        gameStatusObservable = gameState
                .map(GameUtils::calculateGameStatus);

        winner = gameStatusObservable.map(a -> a.isEnded() ? "Winner is: " + a.getWinner() : "");
    }

    public void subscribe() {
        Observable<Pair<GameState, GameSymbol>>
                gameInfoObservable = Observable.combineLatest(
                gameState, playerInTurnObservable, Pair::new);

        Observable<GridPosition> gameNotEndedTouches =
                gridPositionObservable
                        .withLatestFrom(gameStatusObservable, Pair::new)
                        .filter(pair -> !pair.second.isEnded())
                        .map(pair -> pair.first);

        Observable<GridPosition> filteredTouchesEventObservable =
                gameNotEndedTouches.withLatestFrom(gameState,
                        Pair::new)
                .filter(a -> a.second.isEmpty(a.first))
                .map(b -> b.first);

        compositeDisposable.add(
                filteredTouchesEventObservable
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

    public Observable<GameStatus> getGameStatusObservable() {
        return gameStatusObservable;
    }

    public Observable<String> getWinner() {
        return winner;
    }
}
