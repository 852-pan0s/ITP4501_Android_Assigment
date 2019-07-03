package com.exercise.a1520;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class GameActivity extends Activity {
    private String name;
    private ImageView[] handImgView; //0: my right hand, 1: my left hand, 2: opp right hand, 3: opp left hand
    private int[] isRock;  // 0: my right hand, 1: my left hand, 2: opp right hand, 3: opp left hand
    private int[] handImgSrc; // image source


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        long startTime = System.currentTimeMillis();
        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        isRock = new int[4];
        handImgView = new ImageView[4];
        handImgSrc = new int[8];
        handImgView[0] = findViewById(R.id.my_r_hand);
        handImgView[1] = findViewById(R.id.my_l_hand);
        handImgView[2] = findViewById(R.id.opp_r_hand);
        handImgView[3] = findViewById(R.id.opp_l_hand);

        handImgSrc[0] = R.drawable.r_rock;
        handImgSrc[1] = R.drawable.l_rock;
        handImgSrc[2] = R.drawable.r_rock_reverse;
        handImgSrc[3] = R.drawable.l_rock_reverse;
        handImgSrc[4] = R.drawable.r_paper;
        handImgSrc[5] = R.drawable.l_paper;
        handImgSrc[6] = R.drawable.r_paper_reverse;
        handImgSrc[7] = R.drawable.l_paper_reverse;

        for(int i = 0; i<handImgView.length;i++) {
            handImgView[i].setImageResource(handImgSrc[i]);
            final int x = i;
            handImgView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isRock[x] == 0) {
                        handImgView[x].setImageResource(handImgSrc[x + 4]);
                        isRock[x] = 5;
                    } else {
                        handImgView[x].setImageResource(handImgSrc[x]);
                        isRock[x] = 0;
                    }
                    System.out.println("OK");
                }
            });
        }
//
//        handImgView[0].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isRock[0] == 0) {
//                    handImgView[0].setImageResource(handImgSrc[4]);
//                    isRock[0] = 5;
//                } else {
//                    handImgView[0].setImageResource(handImgSrc[0]);
//                    isRock[0] = 0;
//                }
//                System.out.println("OK");
//            }
//        });
//
//        handImgView[3].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isRock[3] == 0) {
//                    handImgView[3].setImageResource(handImgSrc[3]);
//                    isRock[3] = 5;
//                } else {
//                    handImgView[3].setImageResource(handImgSrc[3]);
//                    isRock[3] = 0;
//                }
//            }
//        });
//
//        handImgView[1].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isRock[1] == 0) {
//                    handImgView[1].setImageResource(handImgSrc[1]);
//                    isRock[1] = 5;
//                } else {
//                    handImgView[1].setImageResource(handImgSrc[1]);
//                    isRock[1] = 0;
//                }
//            }
//        });
//
//        handImgView[2].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isRock[2] == 0) {
//                    handImgView[2].setImageResource(handImgSrc[2]);
//                    isRock[2] = 5;
//                } else {
//                    handImgView[2].setImageResource(handImgSrc[2]);
//                    isRock[2] = 0;
//                }
//            }
//        });
    }
}
