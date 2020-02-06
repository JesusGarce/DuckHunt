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

import android.view.View;
import android.widget.TextView;

public class RankingActivity extends AppCompatActivity {
    TextView textPosition;
    TextView textDucks;
    TextView textNick;
    Dialog loadingDialog;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        textPosition = findViewById(R.id.textPositionIndex);
        textDucks = findViewById(R.id.textDucksIndex);
        textNick = findViewById(R.id.textNickIndex);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "starseed.ttf");
        textPosition.setTypeface(typeface);
        textDucks.setTypeface(typeface);
        textNick.setTypeface(typeface);

        MobileAds.initialize(this, "ca-app-pub-3409312019014737/8093290780");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, new UserRankingFragment())
                .commit();
    }

}
