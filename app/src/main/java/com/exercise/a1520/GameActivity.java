package com.exercise.a1520;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class GameActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);

		//startTime = System.currentTimeMillis();

		Intent intent = getIntent(); 

	}
}
