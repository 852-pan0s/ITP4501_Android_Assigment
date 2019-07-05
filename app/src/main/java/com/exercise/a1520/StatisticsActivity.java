package com.exercise.a1520;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class StatisticsActivity extends AppCompatActivity {

    String win = "51";
    String lose = "5";
    int cardinalNum = 5; // cardinal number
    int split = 10; // split how many the axis y of the chart
    int[] chartAxisY = new int[split + 1]; //include 0, so  +1

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawableView(this));
        getSupportActionBar().hide();
        int maxNumberOfGridlines = cardinalNum * split;
        setChartAxisY();
        // set the cardinal number
//        while (max>maxNumberOfGridlines){
//            max -= maxNumberOfGridlines; //because every Y axis of the chart
//            cardinalNum +=5;
//        }


    }

    public int getMax(int n1, int n2) {
        return n1 > n2 ? n1 : n2;
    }

    public void setChartAxisY() {
        int max = getMax(Integer.parseInt(win), Integer.parseInt(lose));
        int i = 1;
        chartAxisY[1] = getFirstChartAxisY(max);
        while (max > chartAxisY[1]) {
            max -= cardinalNum; //because every Y axis of the chart
            chartAxisY[++i] += chartAxisY[i - 1] + cardinalNum;
            Log.d("chartAxisY" + i, "" + chartAxisY[i]);
        }
    }

    public int getFirstChartAxisY(int max) {
        int maxchartAxisY = 0;
        int i = 0;
        while (max > 0) {
            maxchartAxisY += cardinalNum;
            max -= cardinalNum;
        }
        return cardinalNum * (maxchartAxisY / cardinalNum - split + 1);
    }


    class DrawableView extends View {
        private boolean paper;
        private Paint paint;

        public DrawableView(Context context) {
            super(context);
        }

        @Override
        public void onDraw(Canvas canvas) {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            float fSize = 48;
            paint.setTextSize(fSize);
            paint.setTextAlign(Paint.Align.RIGHT);
            float chartStartX = getWidth() / 6f;
            float chartStartY = getHeight() / 2f;
            float chartStopX = chartStartX * 5;
            float chartStopY = chartStartY - chartStartX * 4;
            float cHeight = chartStartY - chartStopY; // height of chart
            float cWeight = chartStopX - chartStartX; // width of chart
            Log.d("StopY", "" + chartStopY);

            //Y axis Legend
            canvas.drawRect(chartStartX, chartStartY, chartStartX * 1.015f, chartStopY, paint);
            //X axis Legend
            canvas.drawRect(chartStartX, chartStartY, chartStopX, chartStartY * 1.003f, paint);

            //draw gridlines of the chart
            float hDistance = cHeight / split; //distance height of line
            for (int i = 0; i <= split; i++) {
                float hLine = hDistance * i;
                float lineStartX = chartStartX * 0.9f;
                float lineStopX = chartStopX;
                float lineStartY = chartStartY - hLine;
                float lineStopY = lineStartY * 1.0015f;  //Rough of line
                float textStartX = lineStartX - fSize / 2;
                float textStartY = chartStartY + fSize / 3f - hLine;
                canvas.drawText("" + chartAxisY[i], textStartX, textStartY, paint);

                //draw line of Chart
                canvas.drawRect(lineStartX, lineStartY, lineStopX, lineStopY, paint);
            }

            float series = 3;
            float wSeries = 180; // width of series
            for (int i = 0; i < series; i++) {

                float wDistance = cWeight / series; // distance width of line
                float lineStartX = chartStartX + wDistance * i;
                float lineStopX = lineStartX + 3; //Rough of line
                float lineStartY = chartStartY * 1.025f;
                float lineStopY = chartStartY;
                paint.setColor(Color.BLACK);
                canvas.drawRect(lineStartX, lineStartY, lineStopX, lineStopY, paint);

                //draw text
                if (i > 0) {
                    float textStartX = lineStartX + fSize;
                    float textStartY = chartStartY + fSize * 1.5f;
                    canvas.drawText(i == 1 ? "Win" : "Lose", textStartX, textStartY, paint);
                }

                //draw series, when i= 1 = draw win  ,  i=2 = draw lose
                if (i > 0) {
                    float temp = i == 1 ? Float.parseFloat(win) : Float.parseFloat(lose);
                    paint.setColor(i == 1 ? Color.GREEN : Color.RED);
                    int y = 1;
                    float seriesStartY = chartStartY;
                    float seriesStartX = lineStartX - wSeries / 2;
                    float seriesStopX = seriesStartX + wSeries;
                    while (temp > 0) {
                        float percent = temp / chartAxisY[y];
                        float seriesStopY = seriesStartY - hDistance * percent;
                        seriesStartY -= seriesStopY;
                        temp -= cardinalNum;
                        canvas.drawRect(seriesStartX, seriesStartY, seriesStopX, seriesStopY, paint);
                    }
                    //float percent = (Float.parseFloat(i == 1 ? win : lose) / cardinalNum);

                }
            }


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
