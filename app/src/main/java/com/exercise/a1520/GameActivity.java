package com.exercise.a1520;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedList;

public class GameActivity extends AppCompatActivity implements ContinueDialog.ContinueListener {
    private ImageView[] imgHand; //0: my right hand, 1: my left hand, 2: opp right hand, 3: opp left hand
    private int[] isRock, imgSrc, win;  // 0: my right hand, 1: my left hand, 2: opp right hand, 3: opp left hand // image source //0 = I win, 1= opponent wins
    private ImageView imgGuess, imgRound, imgAnswer, imgMyCountry, imgOppCountry; //guess view //round img //answer background
    private String myName, myCountry, json, srcLink;
    private String[] opponentInfo; // [0] = id; [1] = name; ; [2] = country
    private DownloadTask task;
    private int myGuess, oppGuess, round;
    private boolean isLoading, resultOk, isRoundEnd, isStopAnimation;
    private ContinueDialog cd;
    private TextView tv_name, tv_oppName, tv_winCount, tv_oppWinCount, tv_round;

    private LinkedList<Opponent> opps;

    private Object[][] player;

    private boolean myRound;
    private Handler mHandler = new Handler();
    private ImageView imgStart;
    private int[] imgStartSrc = {R.drawable.t3, R.drawable.t2, R.drawable.t1, R.drawable.tstart};
    private int imgStartSrcIdx = 0;
    float endX = 100f;
    private ProgressBar timer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        json = intent.getStringExtra("JSON");
        opponentInfo = new String[4];
        srcLink = getResources().getString(R.string.json_src);
        isRock = new int[4];
        imgHand = new ImageView[4];
        imgSrc = new int[99];
        win = new int[2];
        myRound = true;
        cd = new ContinueDialog();

        opps = new LinkedList<>();

        if (MainActivity.getPlayerInfo()) myName = MainActivity.playerInfo[1];
        myCountry = MainActivity.playerInfo[2];

        imgGuess = findViewById(R.id.imgGuess);
        imgRound = findViewById(R.id.imgRound);
        imgAnswer = findViewById(R.id.imgAnswer);
        imgMyCountry = findViewById(R.id.imgMyCountry);
        imgOppCountry = findViewById(R.id.imgOppCountry);
        imgStart = findViewById(R.id.timerStart);
        tv_name = findViewById(R.id.tv_name);
        tv_oppName = findViewById(R.id.tv_oppName);
        tv_winCount = findViewById(R.id.tv_winCount);
        tv_oppWinCount = findViewById(R.id.tv_oppWinCount);
        tv_round = findViewById(R.id.tv_round);
        timer = findViewById(R.id.timer);
        imgAnswer.setImageDrawable(null);

        tv_name.setText(myName);
        try {
            tv_oppName.setText(new JSONObject(json).getString("name"));
        } catch (Exception e) {
            Log.d("JSON get name error", e.getMessage());
        }

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

        setOpponent();
        downloadGuess(100, false);

        mHandler.postDelayed(timerAnimation, 0);

    }

    public void initGame() {
        //set onclike listener for each hand image view
        mHandler.postDelayed(timerStart,10);
        int i = 0;
        for (final ImageView hiv : imgHand) {
            final int x = i++;
            hiv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isRoundEnd) {
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
                    } else {
                        if (win[0] > 1 || win[1] > 1) {
                            cd.show(getSupportFragmentManager(), "Continue");
                        } else {
                            roundStart();
                        }
                    }
                }
            });
        }

        //set on click listener to imgGuess
        imgGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (opps.size() > 0) {
                    if (win[0] > 1 || win[1] > 1) {
                        cd.show(getSupportFragmentManager(), "Continue");
                    } else {
                        if (myRound) {
                            myGuess = getActualHand();
                        }
                        roundStart();
                    }
                } else {
                    Toast.makeText(GameActivity.this, "Loading... Please wate...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Runnable timerAnimation = new Runnable() {
        @Override
        public void run() {
            if (imgStartSrcIdx < 4) { //4
                imgStart.setImageResource(imgStartSrc[imgStartSrcIdx++]);
                ObjectAnimator[] animators = new ObjectAnimator[2];
                animators[0] = ObjectAnimator.ofFloat(imgStart, "scaleX", 0, 1);
                animators[1] = ObjectAnimator.ofFloat(imgStart, "scaleY", 0, 1);

                for (int i = 0; i < animators.length; i++) {
                    animators[i].setDuration(1000);
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animators);
                animatorSet.start();
                mHandler.postDelayed(timerAnimation, 1000);
            } else {
                imgStart.setImageDrawable(null);
                initGame();
                mHandler.postDelayed(repeatAnimation, 0);
            }
        }
    };

    private Runnable repeatAnimation = new Runnable() {
        @Override
        public void run() {
            ObjectAnimator[] animators = new ObjectAnimator[imgHand.length * 2];

            animators[0] = ObjectAnimator.ofFloat(imgHand[0], "rotation", 0, -10f);
            animators[1] = ObjectAnimator.ofFloat(imgHand[0], "rotation", -10f, 0);
            animators[2] = ObjectAnimator.ofFloat(imgHand[1], "rotation", 0, 10f);
            animators[3] = ObjectAnimator.ofFloat(imgHand[1], "rotation", 10f, 0);
            animators[4] = ObjectAnimator.ofFloat(imgHand[2], "rotation", 0, 10f);
            animators[5] = ObjectAnimator.ofFloat(imgHand[2], "rotation", 10f, 0);
            animators[6] = ObjectAnimator.ofFloat(imgHand[3], "rotation", 0, -10f);
            animators[7] = ObjectAnimator.ofFloat(imgHand[3], "rotation", -10f, 0);

            for (int i = 0; i < animators.length; i++) {
                animators[i].setDuration(1000);
            }
            AnimatorSet[] animatorSet = new AnimatorSet[imgHand.length];
            for (int i = 0; i < animatorSet.length; i++) {
                animatorSet[i] = new AnimatorSet();
                animatorSet[i].playSequentially(animators[i * 2], animators[i * 2 + 1]);
                animatorSet[i].start();
            }

            if (isStopAnimation) {
                for (AnimatorSet as : animatorSet) {
                    as.pause();
                }
            } else {
                for (AnimatorSet as : animatorSet) {
                    as.start();
                }
            }
            mHandler.postDelayed(repeatAnimation, 2000);
        }
    };

    private Runnable refreshScore = new Runnable() {
        @Override
        public void run() {
            tv_winCount.setText("" + win[0]);
            tv_oppWinCount.setText("" + win[1]);
        }
    };

    private Runnable timerStart = new Runnable() {
        @Override
        public void run() {
            if(timer.getProgress() == 0){
                timer.setProgress(100);
            }else{
                timer.setProgress(timer.getProgress()-1);
                mHandler.postDelayed(timerStart,10);
            }
        }
    };

    private Runnable fightAnimation = new Runnable() {
        @Override
        public void run() {
            ObjectAnimator[] animators = new ObjectAnimator[4];
            animators[0] = ObjectAnimator.ofFloat(imgStart, "scaleX", 0.5f, 0);
            animators[1] = ObjectAnimator.ofFloat(imgStart, "scaleY", 0.5f, 0);
            animators[2] = ObjectAnimator.ofFloat(imgStart, "X", 200, endX);
            animators[3] = ObjectAnimator.ofFloat(imgStart, "Y", 880, -250);

            for (int i = 0; i < animators.length; i++) {
                animators[i].setDuration(500);
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animators);
            animatorSet.start();
            mHandler.postDelayed(refreshScore, 500);

        }
    };


    public void setCountryImg() {
        String[] country = {myCountry, opponentInfo[2]};
        ImageView[] imgCountry = {imgMyCountry, imgOppCountry};
        int i = 0;
        for (ImageView iv : imgCountry) {
            switch (country[i++]) {
                case "UK":
                case "United Kingdom":
                    iv.setImageResource(R.drawable.uk);
                    break;
                case "HK":
                case "Hong Kong":
                    iv.setImageResource(R.drawable.hk);
                    break;
                case "JP":
                case "Japan":
                    iv.setImageResource(R.drawable.jp);
                    break;
                case "US":
                case "United States":
                    iv.setImageResource(R.drawable.us);
                    break;
                case "CN":
                case "China":
                    iv.setImageResource(R.drawable.cn);
                    break;
            }
        }
    }


    public void roundStart() {
        isRoundEnd = false;
        isStopAnimation=false;
        if (myRound) {
            imgRound.setImageResource(R.drawable.your_round);
        } else {
            imgRound.setImageResource(R.drawable.opp_round);
        }
        if (resultOk) {
            resultOk = false;
            if (win[0] == 0 && win[1] == 0) {
                switchPlayer();
            } else {
                if (myRound) {
                    initRound(true);
                } else {
                    initRound(false);
                }
            }
        } else {
            fight();
        }
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
        imgAnswer.setImageDrawable(null);
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
        imgAnswer.setImageDrawable(null);
    }

    public void setOpponent() {
        try {
            JSONObject jObj = new JSONObject(json);
            opponentInfo[0] = jObj.getString("id");
            opponentInfo[1] = jObj.getString("name");
            opponentInfo[2] = jObj.getString("country");
            setCountryImg();

        } catch (Exception e) {
            Toast.makeText(this, "setOpponent Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void downloadGuess(int count, boolean showDialog) {
        if (task == null || task.getStatus().equals(AsyncTask.Status.FINISHED)) {
            isLoading = true;
            task = new DownloadTask(this, getSupportFragmentManager(), showDialog);
            task.setTitle(String.format("Connecting to the server... (%d/%d)", opps.size(), count));
            task.setDoCount(count);
            task.execute(srcLink + opponentInfo[0]);
        }
    }

    public void setOppGuessJson(String oppJson) {
        try {
            JSONObject jObj = new JSONObject(oppJson);
            int guess = jObj.getInt("guess");
            int right = jObj.getInt("right");
            int left = jObj.getInt("left");
            opps.addLast(new Opponent(opponentInfo[0], opponentInfo[1], opponentInfo[2], left, right, guess));
            //Toast.makeText(this, "Opp: " + oppGuess + "," + isRock[3] + "," + isRock[2], Toast.LENGTH_LONG).show();
            isLoading = false;
        } catch (Exception e) {
            Toast.makeText(this, "setOppJson Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void setHandImg() {
        setGuessImg();
        int i = 0;
        for (final ImageView hiv : imgHand) {
            final int x = i++;
            if (!isLoading && !myRound) {
                if (isRock[x] == 5) {
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
        int handIdx = 0;
        int sum = getActualHand();
        if (!myRound) {
            sum = oppGuess;
        }
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

    public void fight() {
        isStopAnimation=true;
        oppGuess = opps.getFirst().getGuess();
        isRock[2] = opps.getFirst().getLeft();
        isRock[3] = opps.getFirst().getRight();
        opps.removeFirst();
        setHandImg();
        int roundHand = getActualHand();
        if (myRound) {
            endX = 100f;
            imgAnswer.setImageResource(R.drawable.guess_answer);
            if (roundHand == myGuess) {
                imgStart.setImageResource(R.drawable.plus1);
                if (++win[0] >= 2) {
                    imgRound.setImageResource(R.drawable.you_win);
                    saveGameLog(1);
                }
            } else {
                imgStart.setImageResource(R.drawable.minus1);
                win[0] = 0;
            }
        } else {
            endX = 240f;
            imgAnswer.setImageResource(R.drawable.guess_opp);
            if (roundHand == oppGuess) {
                imgStart.setImageResource(R.drawable.plus1);
                if (++win[1] >= 2) {
                    imgRound.setImageResource(R.drawable.you_lose);
                    saveGameLog(0);
                }
            } else {
                imgStart.setImageResource(R.drawable.minus1);
                win[1] = 0;
            }
        }
        mHandler.postDelayed(fightAnimation, 0);
        round++;
        tv_round.setText("Round:" + round);
        resultOk = true;
        isRoundEnd = true;
        isStopAnimation=true;
        Log.d("LinkedList", "" + opps.size());
        if (opps.size() <= 30) {
            downloadGuess(50, false);
        }
    }

    @Override
    public void continueGame(boolean isContinue) {
        if (isContinue) {
            if (!myRound) switchPlayer();
            roundStart();
            for (int i = 0; i < win.length; i++) {
                win[i] = 0;
            }
            tv_winCount.setText("" + win[0]);
            tv_oppWinCount.setText("" + win[1]);
        } else {
            task.cancel(true);
            finish();
        }
    }

//    public void finish() {
//        Intent result = new Intent();
//        result.putExtra("gameFinish", "OK");
//        setResult(RESULT_OK, result);
//        super.finish();
//    }

    public void onBackPressed() {
        //not allow pressing the back button
        task.cancel(true);
        task = null;
        finish();
        super.onBackPressed();
    }

    public void saveGameLog(int win) {
        Date now = Calendar.getInstance().getTime();
        String date = "" + (now.getDate() > 10 ? now.getDate() : "0" + now.getDate());
        String month = "" + (now.getMonth() > 10 ? now.getMonth() : "0" + now.getMonth());
        String hour = "" + (now.getHours() > 10 ? now.getHours() : "0" + now.getHours());
        String minute = "" + (now.getMinutes() > 10 ? now.getMinutes() : "0" + now.getMinutes());
        String second = "" + (now.getSeconds() > 10 ? now.getSeconds() : "0" + now.getSeconds());
        String currentDate = date + "-" + month + "-" + Calendar.getInstance().getWeekYear();
        String curretnTime = hour + ":" + minute + ":" + second;

        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.exercise.a1520/GameDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
            // GameLog (gameDate TEXT, gameTime TEXT, opponentName TEXT, winOrLose INTEGER, PRIMARY KEY(gameDate,gameTime));");
            String sql = String.format("INSERT INTO GameLog VALUES('%s','%s','%s',%d)", currentDate, curretnTime, opponentInfo[1], win);
            db.execSQL(sql);
            Log.d("DB of Game", sql);
            db.close();
        } catch (SQLiteException e) {
            Log.d("Game DB error", e.getMessage());
        }
    }
}
