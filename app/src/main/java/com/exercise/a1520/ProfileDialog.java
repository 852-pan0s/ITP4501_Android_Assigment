package com.exercise.a1520;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileDialog extends DialogFragment {
    private EditText et_name;
    private String[] countries;
    private Spinner spinner;
    private ImageView imgCountry;
    private TextView tv_title;
    private AppCompatActivity activity;
    private int id;
    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            manager.beginTransaction().remove(this).commit();
        } catch (Exception e) {
            Log.e("Dialog","  has not been shown");
        }
        super.show(manager, tag);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.register, null);
        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (et_name.getText().toString().length() > 0) {
                            updateProfile();
                            Toast.makeText(activity, getResources().getString(R.string.save), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(activity, getResources().getString(R.string.emptyName), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProfile();
                        Toast.makeText(activity, getResources().getString(R.string.deleted), Toast.LENGTH_LONG).show();
                    }
                });
        countries = getResources().getStringArray(R.array.country);
        spinner = view.findViewById(R.id.spinner);
        et_name = view.findViewById(R.id.et_name);
        imgCountry = view.findViewById(R.id.imgCountry);
        tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText("Profile");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, countries);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (spinner.getSelectedItem().toString()) {
                    case "UK":
                    case "United Kingdom":
                        imgCountry.setImageResource(R.drawable.uk);
                        break;
                    case "HK":
                    case "Hong Kong":
                        imgCountry.setImageResource(R.drawable.hk);
                        break;
                    case "JP":
                    case "Japan":
                        imgCountry.setImageResource(R.drawable.jp);
                        break;
                    case "US":
                    case "United States":
                        imgCountry.setImageResource(R.drawable.us);
                        break;
                    case "CN":
                    case "China":
                        imgCountry.setImageResource(R.drawable.cn);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        onLoad();

        return builder.create();
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void deleteProfile() {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.exercise.a1520/GameDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
            db.execSQL("DELETE FROM Player");
            Log.d("DB of profile", "DB is ok");
            db.close();
        } catch (SQLiteException e) {
            Log.d("profile error", e.getMessage());
        }
    }

    public void updateProfile() {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.exercise.a1520/GameDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
            db.execSQL(String.format("UPDATE Player SET name = '%s', country ='%s' WHERE id = %d", et_name.getText().toString(), spinner.getSelectedItem().toString(), id));
            Log.d("DB of profile", "DB is ok");
            db.close();
        } catch (SQLiteException e) {
            Log.d("profile error", e.getMessage());
        }
    }

    public void onLoad() {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.exercise.a1520/GameDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);

            Cursor c = db.rawQuery("SELECT * FROM Player", null);
            c.moveToNext();
            id = c.getInt(0);
            et_name.setText(c.getString(1));
            int selectedIdx = 0;
            switch (c.getString(2)) {
                case "HK":
                case "Hong Kong":
                    selectedIdx = 0;
                    break;
                case "UK":
                case "United Kingdom":
                    selectedIdx = 1;
                    break;
                case "JP":
                case "Japan":
                    selectedIdx = 2;
                    break;
                case "US":
                case "United States":
                    selectedIdx = 3;
                    break;
                case "CN":
                case "China":
                    selectedIdx = 4;
                    break;
            }
            spinner.setSelection(selectedIdx);

            Log.d("DB of profile", "DB is ok");
            db.close();
        } catch (SQLiteException e) {
            Log.d("profile error", e.getMessage());
        }
    }

}
