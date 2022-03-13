package com.tehmou.book.androidtictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.jakewharton.rxbinding2.view.RxView;
import com.tehmou.book.androidtictactoe.pojo.GameGrid;
import com.tehmou.book.androidtictactoe.pojo.GameSymbol;
import com.tehmou.book.androidtictactoe.pojo.GridPosition;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.BehaviorSubject;

public class MainActivity extends AppCompatActivity {

    private GameGridViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InteractiveGameGridView gameGridView = (InteractiveGameGridView) findViewById(R.id.grid_view);
        PlayerView playerView = (PlayerView) findViewById(R.id.player_in_turn_image_view);

        viewModel = new GameGridViewModel(gameGridView.getTouchesOnGrid());
        viewModel.subscribe();

        viewModel.getGameGridViewObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gameGridView::setData);

        viewModel.getPlayerInTurnObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(playerView::setData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.unsubscribe();
    }
}
