package com.exercise.a1520;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        getSupportActionBar().hide();
    }

    class DrawableView extends View {
        private boolean paper;
        private Paint paint;
        public DrawableView(Context context) {
            super(context);
        }

//        @Override
//        public void onDraw(Canvas canvas) {
//            paint = new Paint();
//            paint.setAntiAlias(true);
//            paint.setColor(Color.WHITE);
//            float startX = getWidth()/6f;
//            float startY = getHeight()/2f;
//            float stopX = getHeight()/2f;
//            float stopY = getHeight()/2f;
//            canvas.drawLine(startX,startY,stopX,stopY,paint);
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
    }
}
