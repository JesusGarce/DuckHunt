package es.jesusgarce.duckhunt.ui;

import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import es.jesusgarce.duckhunt.R;

import android.view.View;
import android.widget.TextView;

public class RankingActivity extends AppCompatActivity {
    TextView textPosition;
    TextView textDucks;
    TextView textNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        textPosition = findViewById(R.id.textPositionIndex);
        textDucks = findViewById(R.id.textDucksIndex);
        textNick = findViewById(R.id.textNickIndex);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "pixel.ttf");
        textPosition.setTypeface(typeface);
        textDucks.setTypeface(typeface);
        textNick.setTypeface(typeface);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, new UserRankingFragment())
                .commit();
    }

}
