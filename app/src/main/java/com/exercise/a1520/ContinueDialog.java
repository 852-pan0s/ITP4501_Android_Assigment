package com.exercise.a1520;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ContinueDialog extends DialogFragment {
    private ContinueDialog.ContinueListener listener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ContinueDialog.ContinueListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + ": You need to implement ContinueListener.");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.continue_game, null);
        builder.setView(view)
                .setTitle("Continue?")
                .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.continueGame(false);
                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.continueGame(true);
                    }
                });

        return builder.create();
    }

    public interface ContinueListener {
        void continueGame(boolean isContinue);
    }
}
