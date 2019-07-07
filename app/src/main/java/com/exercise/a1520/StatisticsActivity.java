package com.exercise.a1520;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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

    String win = "0";
    String lose = "0";
    String[] seriesName = {"Win", "Lose"};
    int[] seriesColor = {Color.GREEN, Color.RED};
    int cardinalNum = 5; // cardinal number
    int split = 10; // split how many the axis y of the chart
    int[] chartAxisY = new int[split + 1]; //include 0, so  +1

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawableView(this));
        getSupportActionBar().hide();
        setDate();

    }

    public void setDate() {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.exercise.a1520/GameDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
// GameLog (gameDate TEXT, gameTime TEXT, opponentName TEXT, winOrLose INTEGER, PRIMARY KEY(gameDate,gameTime));");
            Cursor c1 = db.rawQuery("SELECT COUNT(*) AS 'winOrLose'FROM GameLog GROUP BY winOrLose HAVING winOrLose ='0'", null);
            Cursor c2 = db.rawQuery("SELECT COUNT(*) AS 'winOrLose'FROM GameLog GROUP BY winOrLose HAVING winOrLose ='1'", null);

            try {
                c1.moveToNext();
                lose = c1.getString(0);
                Log.d("DB of profile", "DB is ok");
            } catch (CursorIndexOutOfBoundsException ce) {
                Log.d("Statistics Error", ce.getMessage());
            }
            try {
                c2.moveToNext();
                win = c2.getString(0);
                Log.d("DB of profile", "DB is ok");
            } catch (CursorIndexOutOfBoundsException ce) {
                Log.d("Statistics Error", ce.getMessage());
            }
            db.close();
        } catch (SQLiteException e) {
            Log.d("profile error", e.getMessage());
        }
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

            float chartStartX = getWidth() / 6f;
            float chartStartY = getHeight() / 2f;
            float chartStopX = chartStartX * 5;
            float chartStopY = chartStartY - chartStartX * 4;
            float cHeight = chartStartY - chartStopY; // height of chart
            float cWeight = chartStopX - chartStartX; // width of chart

            //draw title
            float fSizeTitle = 64;
            Paint titlePaint = new Paint();
            titlePaint.setFlags(Paint.UNDERLINE_TEXT_FLAG);
            titlePaint.setTextSize(fSizeTitle);
            titlePaint.setTextAlign(Paint.Align.CENTER);

            canvas.drawText(getResources().getString(R.string.chart_title), chartStartX + chartStopX / 2f - fSizeTitle, chartStopY - fSizeTitle, titlePaint);

            //set paint for draw the text of axis Y
            paint.setTextAlign(Paint.Align.RIGHT);
            float fSize = 48;
            paint.setTextSize(fSize);
            Log.d("StopY", "" + chartStopY);

            //draw the axis Y of the chart
            canvas.drawRect(chartStartX, chartStartY, chartStartX * 1.015f, chartStopY, paint);
            //draw the axis X of the chart
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

            float series = seriesName.length + 1; // 0 is first series name so +1
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
                if (i > 0 && i <= seriesName.length) {
                    float textStartX = lineStartX + fSize;
                    float textStartY = chartStartY + fSize * 1.5f;
                    canvas.drawText(seriesName[i - 1], textStartX, textStartY, paint);
                }

                //draw series, when i= 1 = draw win  ,  i=2 = draw lose
                if (i == 1 || i == 2) {
                    float temp = i == 1 ? Float.parseFloat(win) : Float.parseFloat(lose); // get win or lose
                    Log.d("temp1", "" + temp);
                    paint.setColor(seriesColor[i - 1]); // set the color of win & lose
                    int y = 1;
                    float seriesStartY = chartStartY;
                    float seriesStartX = lineStartX - wSeries / 2;
                    float seriesStopX = seriesStartX + wSeries;
                    float seriesStopY = seriesStartY * 0.95f;
                    //draw the first chart axis Y (e.g. from 0 to 5, 0 to 10, 0 to 45, etc.)
                    if (temp > chartAxisY[1]) {
                        y = 2;
                        seriesStopY = seriesStartY - hdgLing; //because the temp is greater than the charAxisY, the percentage is 100%, here is hide
                        canvas.drawRect(seriesStartX, seriesStartY, seriesStopX, seriesStopY, paint);
                        seriesStartY = seriesStopY; //set next the start of next chart axis Y
                        temp -= chartAxisY[1]; // make the start to second chart axis Y
                        Log.d("temp2", "" + temp);
                    } else {
                        Log.d("temp2", "" + temp);
                        float percent = temp / chartAxisY[1]; //calculate the percentage of temp over first chart axis Y
                        seriesStopY = seriesStartY - hdgLing * percent;
                        canvas.drawRect(seriesStartX, seriesStartY, seriesStopX, seriesStopY, paint);
                        temp = 0;
                    }
                    while (temp > 0) {
                        float percent = temp > cardinalNum ? 1.0f : temp / cardinalNum; //1.0f = 100%(converted to float
                        seriesStopY = seriesStartY - hdgLing * percent;
                        canvas.drawRect(seriesStartX, seriesStartY, seriesStopX, seriesStopY, paint);
                        Log.d("temp3, percent", "" + temp + "," + percent);
                        seriesStartY = seriesStopY;
                        temp -= cardinalNum;
                        Log.d("seriesStartY", "" + percent);
                    }
                    //draw the number on the top of the series
//                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setColor(Color.BLACK);
                    canvas.drawText(i == 1 ? win : lose, seriesStopX-fSize*1.7f, seriesStopY, paint);
                }
            }

            //draw legend
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setColor(seriesColor[0]); //square for the first series
            canvas.drawRect(chartStartX, chartStartY + 200, chartStartX + 40, chartStartY + 240, paint);
            paint.setColor(seriesColor[1]); //square for the second series
            canvas.drawRect(chartStartX, chartStartY + 270, chartStartX + 40, chartStartY + 310, paint);
            paint.setColor(Color.BLACK); //text
            float legendStopY = chartStartY + 310;
            canvas.drawText(seriesName[0], chartStartX + 80, chartStartY + 240, paint);
            canvas.drawText(seriesName[1], chartStartX + 80, legendStopY, paint);


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
