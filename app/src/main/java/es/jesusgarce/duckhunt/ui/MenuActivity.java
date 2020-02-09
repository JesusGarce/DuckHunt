package es.jesusgarce.duckhunt.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import es.jesusgarce.duckhunt.R;
import es.jesusgarce.duckhunt.common.Constants;
import es.jesusgarce.duckhunt.models.User;
import info.hoang8f.widget.FButton;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MenuActivity extends AppCompatActivity {
    TextView inputNick;
    TextView txtBestScore;
    TextView txtGamesPlayed;
    TextView txtLoadingMenu;
    FButton btnStart;
    FButton btnRanking;
    FButton btnLogout;
    String nick;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    Dialog loadingDialog;
    Context context;
    FirebaseUser user;
    String uid;
    User player;
    ScrollView menuView;
    String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        context = this;

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        user = firebaseAuth.getCurrentUser();
        uid = user.getUid();
        menuView = findViewById(R.id.menuScroll);

        inputNick = findViewById(R.id.txtNick);
        txtBestScore =  findViewById(R.id.txtBestScore);
        txtLoadingMenu = findViewById(R.id.loadingTextMenu);
        txtGamesPlayed = findViewById(R.id.txtGamesPlayed);
        btnStart = findViewById(R.id.buttonStart);
        btnRanking = findViewById(R.id.buttonRanking);
        btnLogout = findViewById(R.id.buttonLogout);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "starseed.ttf");
        inputNick.setTypeface(typeface);
        txtGamesPlayed.setTypeface(typeface);
        txtBestScore.setTypeface(typeface);
        txtLoadingMenu.setTypeface(typeface);
        btnStart.setTypeface(typeface);
        btnRanking.setTypeface(typeface);
        btnLogout.setTypeface(typeface);

        txtLoadingMenu.setVisibility(View.VISIBLE);
        menuView.setVisibility(View.INVISIBLE);

        chargeUser();

        btnStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showChooseLevelDialog();
            }
        });

        btnRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MenuActivity.this, RankingActivity.class);
                startActivity(i);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                LoginManager.getInstance().logOut();
                Intent i = new Intent(MenuActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() { }

    private void showChooseLevelDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_choose_level);
        dialog.setCancelable(false);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "starseed.ttf");

        TextView textSelectLevel = dialog.findViewById(R.id.textChooseLevel);
        FButton levelHard = dialog.findViewById(R.id.dialogLevelHard);
        FButton levelMedium = dialog.findViewById(R.id.dialogLevelMedium);
        FButton levelEasy = dialog.findViewById(R.id.dialogLevelEasy);

        textSelectLevel.setTypeface(typeface);
        levelHard.setTypeface(typeface);
        levelMedium.setTypeface(typeface);
        levelEasy.setTypeface(typeface);

        levelHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                level = Constants.LEVEL_HARD;
                dialog.dismiss();
                loadingDialog.show();
                startGame();
            }
        });

        levelMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                level = Constants.LEVEL_MEDIUM;
                dialog.dismiss();
                loadingDialog.show();
                startGame();
            }
        });

        levelEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                level = Constants.LEVEL_EASY;
                dialog.dismiss();
                loadingDialog.show();
                startGame();
            }
        });

        dialog.show();
    }

    private void chargeUser() {
        loadingDialog = new Dialog(context);
        loadingDialog.setContentView(R.layout.dialog_loading);

        TextView loading = loadingDialog.findViewById(R.id.loadingText);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "starseed.ttf");
        loading.setTypeface(typeface);

        loadingDialog.setCancelable(false);

        //loadingDialog.show();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        player = documentSnapshot.toObject(User.class);
                        inputNick.setText("Hi "+player.getNick()+"!");
                        txtBestScore.setText("Best Score\n"+player.getDucks());
                        txtGamesPlayed.setText("Games Played\n"+player.getGamesPlayed());
                        changeLoginFormVisibility(true);
                        loadingDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MenuActivity.this, "Ups, something bad happened, we can't restore your information",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startGame() {
        Intent i = new Intent(MenuActivity.this, GameActivity.class);
        i.putExtra(Constants.EXTRA_NICK, player.getNick());

        switch (level) {
            case Constants.LEVEL_EASY:
                i.putExtra(Constants.EXTRA_BEST_SCORE, String.valueOf(player.getDucksEasy()));
                break;
            case Constants.LEVEL_MEDIUM:
                i.putExtra(Constants.EXTRA_BEST_SCORE, String.valueOf(player.getDucksMedium()));
                break;
            case Constants.LEVEL_HARD:
                i.putExtra(Constants.EXTRA_BEST_SCORE, String.valueOf(player.getDucksHard()));
                break;
            default:
                i.putExtra(Constants.EXTRA_BEST_SCORE, String.valueOf(player.getDucks()));
                break;
        }

        i.putExtra(Constants.EXTRA_ID, firebaseAuth.getUid());
        i.putExtra(Constants.LEVEL, level);
        loadingDialog.dismiss();
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();

        chargeUser();
    }

    private void changeLoginFormVisibility(boolean showForm) {
        txtLoadingMenu.setVisibility(showForm ? View.GONE : View.VISIBLE);
        menuView.setVisibility(showForm ? View.VISIBLE : View.GONE);
    }
}
