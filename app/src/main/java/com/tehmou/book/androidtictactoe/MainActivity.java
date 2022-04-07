package com.tehmou.book.androidtictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.tehmou.book.androidtictactoe.data.GameModel;
import com.tehmou.book.androidtictactoe.pojo.GameGrid;
import com.tehmou.book.androidtictactoe.pojo.GameSymbol;
import com.tehmou.book.androidtictactoe.pojo.GridPosition;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;

public class MainActivity extends AppCompatActivity {

    private GameGridViewModel viewModel;
    private CompositeDisposable viewBindings;
    private InteractiveGameGridView mGameGridView;
    private PlayerView mPlayerView;
    private TextView mTvWinner;
    private FrameLayout mWinnerView;
    private Button mBtnResetGame;
    private Button btnSaveGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewBindings = new CompositeDisposable();

        mGameGridView = (InteractiveGameGridView) findViewById(R.id.grid_view);
        mPlayerView = (PlayerView) findViewById(R.id.player_in_turn_image_view);
        mTvWinner = (TextView) findViewById(R.id.winner_text_view);
        mWinnerView = (FrameLayout) findViewById(R.id.winner_view);
        mBtnResetGame = (Button) findViewById(R.id.new_game_button);
        btnSaveGame = (Button) findViewById(R.id.btn_save_game);

        GameModel gameModel = new GameModel(this);
        viewModel = new GameGridViewModel(gameModel,
                mGameGridView.getTouchesOnGrid(),
                RxView.clicks(mBtnResetGame),
                RxView.clicks(btnSaveGame));
        viewModel.subscribe();
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeViewBindings();
    }

    private void makeViewBindings() {

        viewBindings.add(viewModel.getGameGridViewObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mGameGridView::setData));

        viewBindings.add(viewModel.getPlayerInTurnObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mPlayerView::setData));

        viewBindings.add(viewModel.getWinner()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mTvWinner::setText));

        viewBindings.add(viewModel.getGameStatusObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(a -> {
                    if (a.isEnded()) {
                        mWinnerView.setVisibility(View.VISIBLE);
                    }
                    else mWinnerView.setVisibility(View.GONE);
                }));
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewBindings.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.unsubscribe();
    }
}
