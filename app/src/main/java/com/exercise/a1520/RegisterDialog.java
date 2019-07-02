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

public class RegisterDialog extends DialogFragment {
    private EditText et_name;
    private RegisterListener listener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (RegisterListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + ": You need to implement RegisterListener.");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.register,null);
        builder.setView(view)
                .setTitle("Enter your name")
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = et_name.getText().toString();
                        if(name.length()>0)
                            listener.getText(name,true);
                        else{
                            listener.getText("Name cannot be empty!",false);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        et_name = view.findViewById(R.id.et_name);

        return builder.create();
    }

    public interface RegisterListener{
        void getText(String name,boolean isName);
    }
}
