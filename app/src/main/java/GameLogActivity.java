import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.exercise.a1520.R;

public class GameLogActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
    }
}