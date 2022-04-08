package com.tehmou.book.androidtictactoe.load;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tehmou.book.androidtictactoe.GameApplication;
import com.tehmou.book.androidtictactoe.R;
import com.tehmou.book.androidtictactoe.data.GameModel;
import com.tehmou.book.androidtictactoe.pojo.SavedGame;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class LoadGameActivity extends AppCompatActivity {

    GameModel gameModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

        // Get the shared GameModel
        gameModel = ((GameApplication) getApplication()).getGameModel();

        SavedGamesListAdapter listAdapter =
                new SavedGamesListAdapter(this, android.R.layout.simple_list_item_1);

        ListView listView = (ListView) findViewById(R.id.saved_games_list);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            SavedGame savedGame = (SavedGame) view.getTag();
            gameModel.putActiveGameState(savedGame.getGameState());
            finish();
        });

        gameModel.getSavedGamesStream()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    listAdapter.clear();
                    listAdapter.addAll(list);
                });
    }
}
