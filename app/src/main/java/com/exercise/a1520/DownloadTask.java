package com.exercise.a1520;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.text.style.TtsSpan;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.support.v4.app.FragmentManager;

public class DownloadTask extends AsyncTask<String, Integer, String> {
    private FragmentManager fm;
    private AppCompatActivity activity;
    private boolean showDialog;
    private LoadingDialog ld;


    public DownloadTask(AppCompatActivity activity, FragmentManager fm, boolean showDialog) {
        this.fm = fm;
        this.activity = activity;
        this.showDialog = showDialog;
    }

    @Override
    protected String doInBackground(String... values) {
        if (showDialog) {
            ld = new LoadingDialog();
            ld.show(fm, "Loading");
        }
        InputStream inputStream = null;
        String result = "";
        URL url = null;
        try {
            url = new URL(values[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection(); // Make GET request
            con.setRequestMethod("GET");
            con.connect();
            inputStream = con.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            inputStream.close();
        } catch (Exception e) {
            result = e.getMessage();
        }
        if (showDialog) ld.dismiss();
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        // Parse to get weather information in String result
        try {
            MainActivity ma = (MainActivity) activity;
            ma.setJson(result);
            ma.startGame();
        } catch (Exception ex) {
            try {
                GameActivity ga = (GameActivity) activity;
                ga.setOppJson(result);
            } catch (Exception e) {
                String error = e.getMessage();
                Log.d("Download Error", error);
            }
        }
    }
}
