package es.jesusgarce.duckhunt.ui;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import es.jesusgarce.duckhunt.R;
import es.jesusgarce.duckhunt.common.Constants;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RankingActivity extends AppCompatActivity {
    TextView textPosition;
    TextView textDucks;
    TextView textNick;
    Button rankingHard;
    Button rankingMedium;
    Button rankingEasy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        textPosition = findViewById(R.id.textPositionIndex);
        textDucks = findViewById(R.id.textDucksIndex);
        textNick = findViewById(R.id.textNickIndex);

        rankingHard = findViewById(R.id.btnRankingHard);
        rankingMedium = findViewById(R.id.btnRankingMedium);
        rankingEasy = findViewById(R.id.btnRankingEasy);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "starseed.ttf");
        textPosition.setTypeface(typeface);
        textDucks.setTypeface(typeface);
        textNick.setTypeface(typeface);

        rankingHard.setTypeface(typeface);
        rankingHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new UserRankingFragment(Constants.LEVEL_HARD_DATABASE))
                        .commit();
            }
        });

        rankingMedium.setTypeface(typeface);
        rankingMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new UserRankingFragment(Constants.LEVEL_MEDIUM_DATABASE))
                        .commit();
            }
        });

        rankingEasy.setTypeface(typeface);
        rankingEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new UserRankingFragment(Constants.LEVEL_EASY_DATABASE))
                        .commit();
            }
        });

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, new UserRankingFragment(Constants.LEVEL_MEDIUM_DATABASE))
                .commit();

    }

}
