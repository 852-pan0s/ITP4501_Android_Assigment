package com.exercise.a1520;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements RegisterDialog.RegisterListener, ConfirmDialog.ConfirmListener {
    private LinearLayout draw;
    private ImageView[] imgView;
    private TextView tv_name;
    private Button btnStart, btnStatistics, btnProfile, btnClear, btnQuit;
    private final int REQUEST_CODE = 8080;
    private DownloadTask task;
    private String name;
    private String json = "";
    private boolean isLoading;
    public static String[] playerInfo; //0=id, 1=name, 2= country
    public boolean rRock, lRock;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

//        draw = findViewById(R.id.draw);
        btnStart = findViewById(R.id.btnStart);
        btnStatistics = findViewById(R.id.btnStatistics);
        btnProfile = findViewById(R.id.btnProfile);
        btnClear = findViewById(R.id.btnClear);
        btnQuit = findViewById(R.id.btnQuit);
        imgView = new ImageView[3];
        imgView[0]=findViewById(R.id.logo);
        imgView[1]=findViewById(R.id.imgLHand);
        imgView[2]=findViewById(R.id.imgRHand);

//        draw.addView(new StartIcon(this));
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPlayerInfo()) {
                    downloadOpponent();
                } else if (!isLoading) openDialog();
            }
        });

        btnStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GameLogActivity.class);
//                Intent i = new Intent(MainActivity.this, GameActivity.class);
                startActivity(i);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getPlayerInfo()) {
                    openDialog();
                } else {
                    ProfileDialog pd = new ProfileDialog();
                    pd.setActivity(MainActivity.this);
                    pd.show(getSupportFragmentManager(), "Profile");
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConfirmDialog().show(getSupportFragmentManager(), "Confirm");
            }
        });

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgView[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lRock)
                    imgView[1].setImageResource(R.drawable.l_paper);
                else
                    imgView[1].setImageResource(R.drawable.l_rock);
                lRock = !lRock;
            }
        });

        imgView[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rRock)
                    imgView[2].setImageResource(R.drawable.r_paper);
                else
                    imgView[2].setImageResource(R.drawable.r_rock);
                rRock = !rRock;
            }
        });

        initialDB(false);


        mHandler.postDelayed(repeatAnimation, 0);
    }

    private Runnable repeatAnimation = new Runnable() {
        @Override
        public void run() {
            ObjectAnimator[] animators = new ObjectAnimator[imgView.length *2];

            animators[0]= ObjectAnimator.ofFloat(imgView[0], "rotationX", 0, 45);
            animators[1]= ObjectAnimator.ofFloat(imgView[0], "rotationX", 45, 0);
            animators[2]= ObjectAnimator.ofFloat(imgView[1], "rotation", 10f, -10f);
            animators[3]= ObjectAnimator.ofFloat(imgView[1], "rotation", -10f, 10f);
            animators[4]= ObjectAnimator.ofFloat(imgView[2], "rotation", -10f, 10f);
            animators[5]= ObjectAnimator.ofFloat(imgView[2], "rotation", 10f, -10f);

            for(int i=0; i<animators.length;i++){
                animators[i].setDuration(1000);
            }
            AnimatorSet[] animatorSet = new AnimatorSet[imgView.length];
            for(int i=0; i<animatorSet.length;i++){
                animatorSet[i] = new AnimatorSet();
                animatorSet[i].playSequentially(animators[i*2], animators[i*2+1]);
                animatorSet[i].start();
            }


            mHandler.postDelayed(repeatAnimation, 2000);
        }
    };

    public void openDialog() {
        new RegisterDialog().show(getSupportFragmentManager(), "Register");
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

    public void initialDB(boolean init) {
        // Create a database if it does not exist
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.exercise.a1520/GameDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);

            if (init) {
                db.execSQL("DROP TABLE IF EXISTS GameLog");
                db.execSQL("DROP TABLE IF EXISTS Player");
            }
            db.execSQL("CREATE TABLE IF NOT EXISTS GameLog (gameDate TEXT, gameTime TEXT, opponentName TEXT, winOrLose INTEGER, PRIMARY KEY(gameDate,gameTime));");
            db.execSQL("CREATE TABLE IF NOT EXISTS Player (id INTEGER PRIMARY KEY AUTOINCREMENT ,name TEXT, country TEXT,dob TEXT, phone TEXT, email TEXT);");
            //Cursor c = db.rawQuery("select * from GameLog ORDER BY gameDate, gameTime", null);
            //c.moveToNext();
            Log.d("DB of MainActivity", "DB is ok");
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void getText(boolean isName) {
        if (!isName) {
            openDialog();
            Toast.makeText(getApplication(), getResources().getString(R.string.emptyName), Toast.LENGTH_LONG).show();
        } else {
            downloadOpponent();
        }
    }

    public void downloadOpponent() {
        if (task == null || task.getStatus().equals(AsyncTask.Status.FINISHED)) {
            task = new DownloadTask(this, getSupportFragmentManager(), true);
            task.execute(getResources().getString(R.string.json_src) + "0");
        }
    }

    public static boolean getPlayerInfo() {
        playerInfo = new String[3];
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.exercise.a1520/GameDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
            Cursor c = db.rawQuery("SELECT * FROM Player", null);
            try {
                c.moveToNext();
                playerInfo[0] = c.getString(0);
                playerInfo[1] = c.getString(1);
                playerInfo[2] = c.getString(2);
                Log.d("getPlayerInfo", String.format("ID:%s, Name:%s ,Country:%s", playerInfo[0], playerInfo[1], playerInfo[2]));
                db.close();
                return true;
            } catch (CursorIndexOutOfBoundsException e) {
                Log.d("Cursor exp", "No record.");
            }
        } catch (SQLiteException e) {
            Log.d("getPlayerInfo ERROR", e.getMessage());
        }
        return false;
    }

    public void startGame() {
        if (json.length() > 0) {
            Intent i = new Intent(MainActivity.this, GameActivity.class);
            i.putExtra("JSON", json);
            //Toast.makeText(getApplication(), json, Toast.LENGTH_LONG).show();
//            startActivityForResult(i,500);
            startActivity(i);
        } else {
            Toast.makeText(this, "Server connection error. Please try in a few minutes later.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void confirm(boolean isYes) {
        if (isYes) {
            initialDB(true);
            Toast.makeText(this, "All records hav been cleared.", Toast.LENGTH_LONG).show();
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            if (data.hasExtra("gameFinish")) {
//                Log.d("MainActivity","restart");
//                Intent i = getIntent();
//                finish();
//                startActivity(i);
//            }
//        }
//    }

//    class StartIcon extends View {
//        private boolean paper;
//
//        public StartIcon(Context context) {
//            super(context);
//        }
//
//        @Override
//        public void onDraw(Canvas canvas) {
//            canvas.drawColor(Color.BLACK);
//            Bitmap icon = null;
//            icon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
//            canvas.drawBitmap(icon, getWidth() * 0.2f, getHeight() * 0.2f, null);
//            //returns bitmap of image in any drawable folder contained in res folder
//            if (paper) {
//                icon = BitmapFactory.decodeResource(getResources(), R.drawable.l_rock);
//            } else {
//                icon = BitmapFactory.decodeResource(getResources(), R.drawable.l_paper);
//            }
//
//
//            //drawBitmap(Bitmap bitmap, float left, float top, Paint paint)
//            //Draw the specified bitmap, with its top/left corner at (x,y),
//            //using the specified paint, transformed by the current matrix.
//            canvas.drawBitmap(icon, getWidth() * 0.32f, getHeight() * 0.45f, null);
//
//        }
//
//        public boolean onTouchEvent(MotionEvent event) {
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                if (event.getX() >= getWidth() * 0.32f && event.getX() <= getWidth() * 0.65f && event.getY() >= getHeight() * 0.4f && event.getY() <= getHeight() * 0.7f)
//                    paper = !paper;
//            }
//            invalidate();
//            return true;
//        }
//    }
}
