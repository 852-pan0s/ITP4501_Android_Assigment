package com.exercise.a1520;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by admin on 16/6/16.
 */
public class Result {

    private Context context;
    private TableLayout tbData;
    private String[] column;

    public Result(Context context, TableLayout tbData, String[] column) {
        this.context = context;
        this.tbData = tbData;
        this.column = column;
    }

    public void fillTable(Cursor cursor) throws SQLiteException {
        tbData.removeAllViews();

        tbData.addView(fillHeader(cursor));
        while (cursor.moveToNext())
            tbData.addView(fillRow(cursor));

    }

    public TableRow fillHeader(Cursor cursor) {
        TableRow tr = new TableRow(context);
        tr.setBackgroundColor(0xfff0ffa8);

        for (int i = 0; i < column.length; i++) {
            tr.addView(fillCell(column[i]));
        }
        return tr;
    }

    public TableRow fillRow(Cursor cursor) {
        TableRow tr = new TableRow(context);
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            if (i == cursor.getColumnCount() - 1) {
                String winOrLose = cursor.getInt(i) == 1 ? "Win" : "Lose";
                tr.addView(fillCell(winOrLose));
            }else
            tr.addView(fillCell(cursor.getString(i)));
        }
        return tr;
    }

    public TextView fillCell(String value) {
        TextView tv = new TextView(context);
        tv.setText(value);
        tv.setPadding(10, 10, 10, 10);
        tv.setTextSize(20f);
        tv.setTextColor(Color.BLACK);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return tv;
    }
}
