package com.tehmou.book.androidtictactoe;

import com.tehmou.book.androidtictactoe.pojo.GameGrid;
import com.tehmou.book.androidtictactoe.pojo.GameSymbol;
import com.tehmou.book.androidtictactoe.pojo.GridPosition;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;

public class GameGridViewModel {

    private CompositeDisposable compositeDisposable;
    private BehaviorSubject<GameGrid> gameGridSubject;
    private Observable<GridPosition> gridPositionObservable;

    public GameGridViewModel(Observable<GridPosition> gridPositionObservable) {
        GameGrid emptyGameGrid = new GameGrid(3, 3);
        gameGridSubject = BehaviorSubject.createDefault(emptyGameGrid);
        compositeDisposable = new CompositeDisposable();
        this.gridPositionObservable = gridPositionObservable;
    }

    public void subscribe() {
        compositeDisposable.add(
                gridPositionObservable
                        .withLatestFrom(
                                gameGridSubject,
                                (gridPosition, gameGrid) ->
                                        gameGrid.setSymbolAt(
                                                gridPosition, GameSymbol.CIRCLE)
                        )
                        .subscribe(gameGridSubject::onNext)
        );
    }

    public void unsubscribe() {
        compositeDisposable.clear();
    }

    public Observable<GameGrid> getGameGridViewObservable() {
        return gameGridSubject.hide();
    }
}
