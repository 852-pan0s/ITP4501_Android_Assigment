package com.exercise.a1520;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

public class GameLogActivity extends AppCompatActivity {
    //GameLog (gameDate, gameTime, opponentName, winOrLose);

    private String[] column = {"Date", "Time", "   Opponent   ", "Win?"};
    private TableLayout tbData;
    private Result result;
    private Button btnStatistics;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_log);
        getSupportActionBar().hide();
        result = new Result(this, (TableLayout) findViewById(R.id.tbData),column);;
        btnStatistics = findViewById(R.id.btnStatistics);
        showTable();
        btnStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GameLogActivity.this, StatisticsActivity.class);
                startActivity(i);
            }
        });
    }

    public void showTable() {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.exercise.a1520/GameDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
//            result.fillTable(db.query("GameLog", columns, null, null, null, null, null));
            result.fillTable(db.rawQuery("SELECT * FROM GameLog ORDER BY gameDate DESC ,gameTime DESC",null));
            db.close();
        } catch (SQLiteException e) {
            Log.d("DB of game log", e.getMessage());
        }
    }
}
