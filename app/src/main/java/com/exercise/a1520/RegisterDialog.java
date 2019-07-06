package com.exercise.a1520;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterDialog extends DialogFragment {
    private EditText et_name;
    private RegisterListener listener;
    private String[] countries;
    private Spinner spinner;
    private ImageView imgCountry;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (RegisterListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + ": You need to implement RegisterListener.");
        }
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
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = et_name.getText().toString();
                        if (name.length() > 0) {
                            register();
                            listener.getText(true);
                        } else {
                            listener.getText(false);
                        }
                    }
                });
        countries = getResources().getStringArray(R.array.country);
        spinner = view.findViewById(R.id.spinner);
        et_name = view.findViewById(R.id.et_name);
        imgCountry = view.findViewById(R.id.imgCountry);

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

        return builder.create();
    }

    public interface RegisterListener {
        void getText(boolean isName);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            manager.beginTransaction().remove(this).commit();
        } catch (Exception e) {
            Log.e("Dialog","  has not been shown");
        }
        super.show(manager, tag);
    }

    public void register() {
        // Create a database if it does not exist
        String name = et_name.getText().toString();
        String country = spinner.getSelectedItem().toString();
        if (country.length() == 0) country = countries[0];
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.exercise.a1520/GameDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);

//            db.execSQL("DROP TABLE IF EXISTS GameLog");
//            db.execSQL("DROP TABLE IF EXISTS Player");
            String sql = "INSERT INTO PLAYER VALUES(null,'" + name + "','" + country + "')";
            db.execSQL(sql);
            Log.d("DB of Register", sql);
            db.close();
        } catch (SQLiteException e) {
            Log.d("Register DB error", e.getMessage());
        }
    }
}
