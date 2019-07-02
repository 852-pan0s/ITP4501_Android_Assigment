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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private boolean alarm;
    private LinearLayout draw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        draw = findViewById(R.id.draw);
        draw.addView(new StartIcon(this));

        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.game);
                Intent i = new Intent(MainActivity.this, GameActivity.class);
            }
        });

        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialDB();
            }
        });
    }

    public void initialDB() {
        // Create a database if it does not exist
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.exercise.a1520/GameDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);

            db.execSQL("DROP TABLE IF EXISTS GameLog;");

            db.execSQL("CREATE TABLE GameLog (gameDate TEXT, gameTime TEXT, opponentName TEXT, winOrLose INTEGER, PRIMARY KEY(gameDate,gameTime));");

            db.execSQL("INSERT INTO GameLog VALUES('2016', '02-02', 'Susan', 'W'); ");

            Toast.makeText(this, "Table Seller is created and initialised.",
                    Toast.LENGTH_SHORT).show();

            Cursor c  = db.rawQuery("select * from GameLog ORDER BY gameDate, gameTime", null);
            c.moveToNext();
            Toast.makeText(this, c.getString(2),
                    Toast.LENGTH_LONG).show();
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    class StartIcon extends View {
        private boolean isFlash;
        private boolean start = true;

        public StartIcon(Context context) {
            super(context);
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawColor(Color.BLACK);
            Bitmap icon = null;
            icon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            canvas.drawBitmap(icon, getWidth()*0.2f, getHeight()*0.2f, null);
            //returns bitmap of image in any drawable folder contained in res folder
            if (!isFlash) {
                isFlash = true;
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.l_rock);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    //Nothing
                }
            } else {
                icon = BitmapFactory.decodeResource(getResources(), R.drawable.l_paper);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    //Nothing
                }
                isFlash = false;
            }
            //drawBitmap(Bitmap bitmap, float left, float top, Paint paint)
            //Draw the specified bitmap, with its top/left corner at (x,y),
            //using the specified paint, transformed by the current matrix.
            canvas.drawBitmap(icon, getWidth()*0.32f, getHeight()*0.45f, null);
            invalidate(); //real-time drawing by calling onDraw again
        }

    }
}
