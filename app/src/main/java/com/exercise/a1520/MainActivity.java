package com.exercise.a1520;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements RegisterDialog.RegisterListener {
    private LinearLayout draw;
    private TextView tv_name;
    private Button btnStart, btnStatistics, btnProfile, btnQuit;
    private final int REQUEST_CODE = 8080;
    private DownloadTask task;
    private String name;
    private String json;
    private boolean startGame;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        draw = findViewById(R.id.draw);
        btnStart = findViewById(R.id.btnStart);
        btnStatistics = findViewById(R.id.btnStatistics);
        btnProfile = findViewById(R.id.btnProfile);
        btnQuit = findViewById(R.id.btnQuit);
        draw.addView(new StartIcon(this));
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLoading) openDialog();
            }
        });

        btnStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this ,StatisticsActivity.class);
//                Intent i = new Intent(MainActivity.this, GameActivity.class);
                startActivity(i);
            }
        });

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initialDB();
    }

    public void openDialog() {
        RegisterDialog rd = new RegisterDialog();
        rd.show(getSupportFragmentManager(), "Register");
    }

    public String getName() {
        return name;
    }

    public String getJson() {
        return json;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public void initialDB() {
        // Create a database if it does not exist
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.exercise.a1520/GameDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);

            db.execSQL("CREATE TABLE IF NOT EXISTS GameLog (gameDate TEXT, gameTime TEXT, opponentName TEXT, winOrLose INTEGER, PRIMARY KEY(gameDate,gameTime));");
            db.execSQL("DELETE FROM GameLog");
            Cursor c = db.rawQuery("select * from GameLog ORDER BY gameDate, gameTime", null);
            c.moveToNext();
            Log.d("DB of MainActivity","DB is ok");
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void getText(String name, boolean isName) {
        if (isName && (task == null || task.getStatus().equals(AsyncTask.Status.FINISHED))) {
            setName(name);
            task = new DownloadTask(this, getSupportFragmentManager(),true);
            task.execute(getResources().getString(R.string.json_src) + "0");
            startGame = false;
        } else {
            openDialog();
            Toast.makeText(getApplication(), name, Toast.LENGTH_LONG).show();
        }
    }

    public void startGame() {
        if (!startGame) {
            startGame = true;
            Intent i = new Intent(MainActivity.this, GameActivity.class);
            i.putExtra("name", name);
            i.putExtra("JSON", json);
            //Toast.makeText(getApplication(), json, Toast.LENGTH_LONG).show();
            startActivityForResult(i,500);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data.hasExtra("gameFinish")) {
                Log.d("MainActivity","restart");
                Intent i = getIntent();
                finish();
                startActivity(i);
            }
        }
    }

    class StartIcon extends View {
        private boolean paper;

        public StartIcon(Context context) {
            super(context);
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawColor(Color.BLACK);
            Bitmap icon = null;
            icon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            canvas.drawBitmap(icon, getWidth() * 0.2f, getHeight() * 0.2f, null);
            //returns bitmap of image in any drawable folder contained in res folder
            if (paper) {
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.l_rock);
            } else {
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.l_paper);
            }


            //drawBitmap(Bitmap bitmap, float left, float top, Paint paint)
            //Draw the specified bitmap, with its top/left corner at (x,y),
            //using the specified paint, transformed by the current matrix.
            canvas.drawBitmap(icon, getWidth() * 0.32f, getHeight() * 0.45f, null);

        }

        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (event.getX() >= getWidth() * 0.32f && event.getX() <= getWidth() * 0.65f && event.getY() >= getHeight() * 0.4f && event.getY() <= getHeight() * 0.7f)
                    paper = !paper;
            }
            invalidate();
            return true;
        }
    }
}
