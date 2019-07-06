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

    String win = "55";
    String lose = "30";
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


    }

    public int getMax(int n1, int n2) {
        return n1 > n2 ? n1 : n2;
    }

    public void setChartAxisY() {
        int max = getMax(Integer.parseInt(win), Integer.parseInt(lose));
        chartAxisY[1] = getFirstChartAxisY(max);
        int i = 1;
        if (max < cardinalNum * split) {
            int index = split - 1; // second index of axis Y have been set.
            while (index > 0) {
                chartAxisY[++i] += chartAxisY[i - 1] + cardinalNum;
                Log.d("chartAxisY" + i, "" + chartAxisY[i]);
                index--;
            }
        } else {
            while (max > chartAxisY[1]) {
                max -= cardinalNum; //because every Y axis of the chart
                chartAxisY[++i] += chartAxisY[i - 1] + cardinalNum;
                Log.d("chartAxisY" + i, "" + chartAxisY[i]);
            }
        }
    }

    public int getFirstChartAxisY(int max) {
        if (max < cardinalNum * split) {
            return cardinalNum;
        }
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
            float hdgLing = cHeight / split; //height of distance  of gridline
            for (int i = 0; i <= split; i++) {
                float hGline = hdgLing * i; // height of gridline
                float gLineStartX = chartStartX * 0.9f;
                float gLineStopX = chartStopX;
                float gLineStartY = chartStartY - hGline;
                float gLineStopY = gLineStartY * 1.0015f;  //Rough of gridlines
                float textStartX = gLineStartX - fSize / 2; // start X of text of chart axis Y
                float textStartY = chartStartY + fSize / 3f - hGline; //start Y of text of chart axis Y
                canvas.drawText("" + chartAxisY[i], textStartX, textStartY, paint); // draw text for the chart axis Y

                //draw gridlines of the chart
                canvas.drawRect(gLineStartX, gLineStartY, gLineStopX, gLineStopY, paint);
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
                if (i == 1 || i == 2) {
                    float temp = i == 1 ? Float.parseFloat(win) : Float.parseFloat(lose); // get win or lose
                    Log.d("temp1", "" + temp);
                    paint.setColor(i == 1 ? Color.GREEN : Color.RED); // set the color of win & lose
                    int y = 1;
                    float seriesStartY = chartStartY;
                    float seriesStartX = lineStartX - wSeries / 2;
                    float seriesStopX = seriesStartX + wSeries;
                    //draw the first chart axis Y (e.g. from 0 to 5, 0 to 10, 0 to 45, etc.)
                    if (temp > chartAxisY[1]) {
                        y = 2;
                        float seriesStopY = seriesStartY - hdgLing; //because the temp is greater than the charAxisY, the percentage is 100%, here is hide
                        canvas.drawRect(seriesStartX, seriesStartY, seriesStopX, seriesStopY, paint);
                        seriesStartY = seriesStopY; //set next the start of next chart axis Y
                        temp -= chartAxisY[1]; // make the start to second chart axis Y
                        Log.d("temp2", "" + temp);
                    } else {
                        Log.d("temp2", "" + temp);
                        float percent = temp / chartAxisY[1]; //calculate the percentage of temp over first chart axis Y
                        float seriesStopY = seriesStartY - hdgLing * percent;
                        canvas.drawRect(seriesStartX, seriesStartY, seriesStopX, seriesStopY, paint);
                        temp = 0;
                    }
                    while (temp > 0) {
                        float percent = temp > cardinalNum ? 1.0f : temp / cardinalNum; //1.0f = 100%(converted to float
                        float seriesStopY = seriesStartY - hdgLing * percent;
                        canvas.drawRect(seriesStartX, seriesStartY, seriesStopX, seriesStopY, paint);
                        Log.d("temp3, percent", "" + temp + "," + percent);
                        seriesStartY = seriesStopY;
                        temp -= cardinalNum;
                        Log.d("seriesStartY", "" + percent);
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
