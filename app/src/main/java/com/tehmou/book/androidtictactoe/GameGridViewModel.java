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
    public static final int GRID_WIDTH = 7;
    public static final int GRID_HEIGHT = 7;

    private CompositeDisposable compositeDisposable;
    private BehaviorSubject<GameState> gameState;
    private Observable<GridPosition> gridPositionObservable;
    private Observable<GameSymbol> playerInTurnObservable;
    private Observable<GameStatus> gameStatusObservable;
    private Observable<String> winner;
    private Observable<Object> resetGameEventObservable;

    public GameGridViewModel(Observable<GridPosition> gridPositionObservable, Observable<Object> resetGameEvent) {
        GameGrid emptyGameGrid = new GameGrid(GRID_WIDTH, GRID_HEIGHT);
        GameState emptyGameState = new GameState(emptyGameGrid, GameSymbol.EMPTY);
        gameState = BehaviorSubject.createDefault(emptyGameState);
        compositeDisposable = new CompositeDisposable();
        this.gridPositionObservable = gridPositionObservable;
        this.resetGameEventObservable = resetGameEvent;
        playerInTurnObservable = gameState.map(GameState::getLastPlayedSymbol)
        .map(lastSymbol -> {
            if (lastSymbol == GameSymbol.RED)
                return GameSymbol.BLACK;
            else
                return GameSymbol.RED;
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

        Observable<GridPosition> droppedTouchesGridPosition =
                gameNotEndedTouches
                        .withLatestFrom(gameState, Pair::new)
                        .map(pair -> {
                            GridPosition position = pair.first;
                            GameGrid gameGrid = pair.second.getGameGrid();

                            int i = gameGrid.getHeight() - 1;
                            for (; i >= -1; i--) {
                                if (i == -1) {
                                // Let -1 fall through
                                    break;
                                }
                                GameSymbol symbol =
                                        gameGrid.getSymbolAt(
                                                position.getX(), i);
                                if (symbol == GameSymbol.EMPTY) {
                                    break;
                                }
                            }
                            return new GridPosition(
                                    position.getX(), i);
                        });

        Observable<GridPosition> filteredTouchesEventObservable =
                droppedTouchesGridPosition
                .filter(a -> a.getY() >= 0);

        compositeDisposable.add(
                resetGameEventObservable.map(e -> {
                    GameGrid emptyGameGrid = new GameGrid(GRID_WIDTH, GRID_HEIGHT);
                    GameState emptyGameState = new GameState(emptyGameGrid, GameSymbol.EMPTY);
                    return emptyGameState;
                })
                .subscribe(gameState::onNext)
        );

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
