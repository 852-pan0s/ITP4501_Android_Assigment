package com.exercise.a1520;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

public class GameActivity extends AppCompatActivity {
    private String name;
    private ImageView[] imgHand; //0: my right hand, 1: my left hand, 2: opp right hand, 3: opp left hand
    private int[] isRock;  // 0: my right hand, 1: my left hand, 2: opp right hand, 3: opp left hand
    private int[] imgSrc; // image source
    private ImageView imgGuess; //guess view
    private ImageView imgRound;
    private int[] win;  //0 = I win, 1= opponent wins
    private int[] guess;  //0 = I guess, 1= opponent guesses
    private String json;
    private String srcLink;
    private String[] opponentInfo; // [0] = id; [1] = name; ; [2] = country
    private DownloadTask task;
    private int oppGuess;
    private boolean isLoading;

    private boolean myRound;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        getSupportActionBar().hide();

        long startTime = System.currentTimeMillis();
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        json = intent.getStringExtra("JSON");
        opponentInfo = new String[4];
        srcLink = getResources().getString(R.string.json_src);
        isRock = new int[4];
        imgHand = new ImageView[4];
        imgSrc = new int[99];
        win = new int[2];
        guess = new int[2];
        myRound = true;
        imgGuess = findViewById(R.id.imgGuess);
        imgRound = findViewById(R.id.imgRound);

        imgHand[0] = findViewById(R.id.my_r_hand);
        imgHand[1] = findViewById(R.id.my_l_hand);
        imgHand[2] = findViewById(R.id.opp_r_hand);
        imgHand[3] = findViewById(R.id.opp_l_hand);

        imgSrc[0] = R.drawable.r_rock; //my right rock
        imgSrc[1] = R.drawable.l_rock; //my left rock
        imgSrc[2] = R.drawable.r_rock_reverse; //opponent right rock
        imgSrc[3] = R.drawable.l_rock_reverse; //opponent left rock
        imgSrc[4] = R.drawable.r_paper; //my right paper
        imgSrc[5] = R.drawable.l_paper; //my left paper
        imgSrc[6] = R.drawable.r_paper_reverse; //opponent right paper
        imgSrc[7] = R.drawable.l_paper_reverse; //opponent left paper
        imgSrc[8] = R.drawable.guess_0; //guess 0
        imgSrc[9] = R.drawable.guess_5; //guess 5
        imgSrc[10] = R.drawable.guess_10; //guess 10
        imgSrc[11] = R.drawable.guess_15; //guess 15
        imgSrc[12] = R.drawable.guess_20; //guess 20
        imgSrc[13] = R.drawable.opponent_guess; //opponent guess

        //set onclike listener for each hand image view
        int i = 0;
        for (final ImageView hiv : imgHand) {
            final int x = i++;
            hiv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isLoading && (myRound || x <= 1)) {
                        if (isRock[x] == 0) {
                            // set image to paper
                            hiv.setImageResource(imgSrc[x + 4]);
                            isRock[x] = 5;
                        } else {
                            //set image to rock
                            hiv.setImageResource(imgSrc[x]);
                            isRock[x] = 0;
                        }
                        if (myRound) {
                            setGuessImg();
                        }
                    }
                }
            });
        }

        //set on click listener to imgGuess
        imgGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoading) fight();
            }
        });

        setOpponent();

    }

    public int getActualHand() {
        int sum = 0;
        for (int i = 0; i < isRock.length; i++)
            sum += isRock[i];
        return sum;
    }

    public void initRound(boolean showAll) {
        //reset all the hand (value) to rock
        for (int i = 0; i < isRock.length; i++)
            isRock[i] = 0;

        //reset win
        for (int i = 0; i < win.length; i++) {
            win[i] = 0;
            guess[i] = 0;
        }

        for (int i = 0; i < imgHand.length; i++) {
            if (showAll || i <= 1) {
                imgHand[i].setImageResource(imgSrc[i]);
            } else if (i >= 2)  // 0 to 1 = my hand, >=2 opponent's hand
                imgHand[i].setImageDrawable(null); // no image
        }
        if (showAll) {
            //set guess to 0
            imgGuess.setImageResource(imgSrc[8]);
        } else {
            //set guess to ??
            imgGuess.setImageResource(imgSrc[13]);
        }
    }

    public void switchPlayer() {
        //switch player
        if (myRound = !myRound) {
            imgRound.setImageResource(R.drawable.your_round);
            initRound(true);
        } else {
            imgRound.setImageResource(R.drawable.opp_round);
            initRound(false);
        }
    }

    public void setOpponent() {
        try {
            JSONObject jObj = new JSONObject(json);
            opponentInfo[0] = jObj.getString("id");
            opponentInfo[1] = jObj.getString("name");
            opponentInfo[2] = jObj.getString("country");
            setGuessImg();
        } catch (Exception e) {
            Toast.makeText(this, "setOpponent Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void fight() {
        if (task == null || task.getStatus().equals(AsyncTask.Status.FINISHED)) {
            isLoading = true;
            task = new DownloadTask(this, getSupportFragmentManager(), false);
            task.execute(srcLink + opponentInfo[0]);
        }
    }

    public void setOppJson(String oppJson) {
        try {
            JSONObject jObj = new JSONObject(oppJson);
            isRock[3] = jObj.getInt("left"); //
            isRock[2] = jObj.getInt("right");
            oppGuess = jObj.getInt("guess");
            Toast.makeText(this, "Opp: " + oppGuess + "," + isRock[3] + "," + isRock[2], Toast.LENGTH_LONG).show();
            isLoading = false;
            setHandImg();
            switchPlayer();
        } catch (Exception e) {
            Toast.makeText(this, "setOppJson Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void setHandImg() {
        setGuessImg();
        int i = 0;
        for (final ImageView hiv : imgHand) {
            final int x = i++;
            if (!isLoading) {
                if (isRock[x] != 0) {
                    // set image to paper
                    hiv.setImageResource(imgSrc[x + 4]);
                } else {
                    //set image to rock
                    hiv.setImageResource(imgSrc[x]);
                }
            }
        }
    }

    public void setGuessImg() {
        int sum = getActualHand();
        int handIdx = 0;
        switch (sum) {
            case 0:
                handIdx = 8;
                break;
            case 5:
                handIdx = 9;
                break;
            case 10:
                handIdx = 10;
                break;
            case 15:
                handIdx = 11;
                break;
            case 20:
                handIdx = 12;
                break;
        }
        imgGuess.setImageResource(imgSrc[handIdx]);
    }

    class DrawHP extends View {
        private boolean paper;

        public DrawHP(Context context) {
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
