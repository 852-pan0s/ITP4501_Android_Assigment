package com.exercise.a1520;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class LoadingDialog extends DialogFragment {
    private TextView tv_loading;
    public static String title;

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
        View view = inflater.inflate(R.layout.loading, null);
        builder.setView(view);
        tv_loading = view.findViewById(R.id.tv_loading);

        if (title.length() > 0)
            tv_loading.setText(title);
        else {
            tv_loading.setText("Loading...");
        }
        return builder.create();
    }

}
