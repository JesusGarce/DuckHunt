package es.jesusgarce.duckhunt.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import es.jesusgarce.duckhunt.R;
import es.jesusgarce.duckhunt.common.Constants;
import es.jesusgarce.duckhunt.models.Duck;
import es.jesusgarce.duckhunt.models.User;
import info.hoang8f.widget.FButton;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    TextView nickText;
    TextView counterText;
    TextView timeText;
    TextView bestScoreText;
    TextView counterTextNew;
    TextView counterTextEvent;
    ImageView btnReiniciar;
    ImageView btnClose;

    ConstraintLayout layout;

    int counter = 0;
    int counterStreak = 0;
    int widthScreen;
    int heightScreen;
    Random random;
    boolean gameOver = false;

    String idUser;
    String nick;
    FirebaseUser user;
    FirebaseFirestore db;
    User player;
    String uid;
    String bestScore;
    FirebaseAuth firebaseAuth;
    String level;
    int levelInterval;

    LinkedList<Duck> ducks;
    Duck duck;
    int duckId;

    CountDownTimer generatorDucksTimer;
    CountDownTimer timer;

    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        uid = user.getUid();
        db = FirebaseFirestore.getInstance();

        MobileAds.initialize(this);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3409312019014737/8093290780");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        initScreen();
        initViewComponents();
        initInterval();
        generateDucks();
        initCountDown();
        chargeUser();

        ConstraintLayout activityGame = findViewById(R.id.layoutGame);
        activityGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter--;
                counterStreak = 0;
                counterText.setText(String.valueOf(counter));
                counterTextNew.setText("-" + String.valueOf(1));
                counterTextEvent.setVisibility(View.VISIBLE);
                counterTextEvent.setText("BAD SHOT!");
                counterTextNew.setVisibility(View.VISIBLE);

                new Handler().postDelayed(() -> {
                    counterTextNew.setVisibility(View.INVISIBLE);
                    counterTextEvent.setVisibility(View.INVISIBLE);
                }, 100);
            }
        });
    }

    private void initInterval() {
        if (level.equals(Constants.LEVEL_HARD))
            levelInterval = Constants.LEVEL_HARD_DUCKS_APPEAR_INTERVAL;
        else if (level.equals(Constants.LEVEL_EASY))
            levelInterval = Constants.LEVEL_EASY_DUCKS_APPEAR_INTERVAL;
        else
            levelInterval = Constants.LEVEL_MEDIUM_DUCKS_APPEAR_INTERVAL;
    }

    private void generateDucks() {
        ducks = new LinkedList<>();
        duckId = 0;

        generatorDucksTimer = new CountDownTimer(60000, levelInterval){

            @Override
            public void onTick(long l) {
                duck = new Duck(duckId, level);
                duck.generateImageView(createDuckView(duck), widthScreen, heightScreen);
                duckId++;
                duck.getView().setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View view) {
                        if (!gameOver) {
                            Optional<Duck> duckListOptional = ducks.stream().filter(d -> d.getView().equals(view)).findFirst();
                            if (duckListOptional.isPresent()) {
                                Duck duckList = duckListOptional.get();
                                int value = duckList.duckValue();
                                counter = counter + value;
                                counterText.setText(String.valueOf(counter));
                                counterTextNew.setText("+" + String.valueOf(value));
                                counterTextNew.setVisibility(View.VISIBLE);
                                counterStreak++;

                                if (counterStreak % 5 == 0){
                                    counterTextEvent.setText(counterStreak+ " DUCK STREAK!");
                                    counterTextEvent.setVisibility(View.VISIBLE);
                                }

                                drawHuntedDuck(duckList);

                                new Handler().postDelayed(() -> {
                                    duckList.duckHunted();
                                    counterTextNew.setVisibility(View.INVISIBLE);
                                    counterTextEvent.setVisibility(View.INVISIBLE);
                                    layout.removeView(duckList.getView());
                                    ducks.remove(duckList);
                                }, 100);
                            }
                        }
                    }
                });
                duck.startDuck();
                ducks.addLast(duck);

            }

            @Override
            public void onFinish() {
                ducks = new LinkedList<>();
            }
        };

        generatorDucksTimer.start();

    }

    private ImageView createDuckView(Duck duck) {
        ImageView iv = new ImageView(getApplicationContext());
        switch (duck.getType()){
            case 2:
                if (duck.isLeft())
                    iv.setImageDrawable(getDrawable(R.drawable.duck_left_2));
                else
                    iv.setImageDrawable(getDrawable(R.drawable.duck_2));
                break;
            case 3:
                if (duck.isLeft())
                    iv.setImageDrawable(getDrawable(R.drawable.duck_left_3));
                else
                    iv.setImageDrawable(getDrawable(R.drawable.duck_3));
                break;
            default:
                if (duck.isLeft())
                    iv.setImageDrawable(getDrawable(R.drawable.duck_left_1));
                else
                    iv.setImageDrawable(getDrawable(R.drawable.duck_1));
        }
        final float scale = getResources().getDisplayMetrics().density;
        int dpWidthInPx  = (int) (80 * scale);
        int dpHeightInPx = (int) (80 * scale);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(dpWidthInPx, dpHeightInPx);
        iv.setLayoutParams(lp);
        layout.addView(iv);
        return iv;
    }


    @Override
    public void onBackPressed() {
    }

    private void initCountDown() {
        timer = new CountDownTimer(60000, 1000){
            @Override
            public void onTick(long l) {
                long remainingSeconds = l / 1000;
                if (remainingSeconds<10)
                    timeText.setText("0:0"+remainingSeconds);
                else
                    timeText.setText("0:" + remainingSeconds);
            }

            @Override
            public void onFinish() {
                timeText.setText("0:00");
                gameOver = true;

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }

                showDialogGameOver();
            }
        };

        timer.start();
    }


    private void showDialogGameOver() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_end);
        dialog.setTitle("GAME OVER");
        dialog.setCancelable(false);
        actualizarPuntuacion(counter);
        deleteDucks();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "starseed.ttf");

        TextView textGameOver = dialog.findViewById(R.id.textGameOver);
        TextView textResult = dialog.findViewById(R.id.textResultGO);

        if (counter > 0)
            textResult.setText("You have got "+counter + " points.");
        else
            textResult.setText("All ducks fly freely. Try again.");

        textResult.setTypeface(typeface);
        textGameOver.setTypeface(typeface);

        ImageView dialogShare = dialog.findViewById(R.id.dialogButtonShare);
        dialogShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi guys! I've got "+counter+ " points, can you be better than me? Try to improve that. Play Duck Hunt! ");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        FButton dialogReiniciar = dialog.findViewById(R.id.dialogButtonReiniciar);
        dialogReiniciar.setTypeface(typeface);
        dialogReiniciar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        counter = 0;
                        counterText.setText("0");
                        gameOver = false;
                        generateDucks();
                        initCountDown();
                        dialog.dismiss();
                    }
        });

        FButton dialogVerRanking = dialog.findViewById(R.id.dialogButtonGoHome);
        dialogVerRanking.setTypeface(typeface);
        dialogVerRanking.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Intent i = new Intent(GameActivity.this, MenuActivity.class);
                        startActivity(i);
                        finish();
                    }
                });

        dialog.show();
    }

    private void deleteDucks() {
        for (Duck duck : ducks){
            duck.duckHunted();
            layout.removeView(duck.getView());
        }
        ducks = new LinkedList<>();
    }

    private void initScreen() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        heightScreen = size.y;
        widthScreen = size.x;

        random = new Random();
    }

    private void initViewComponents() {
        nickText = findViewById(R.id.nickText);
        counterText = findViewById(R.id.counterText);
        timeText = findViewById(R.id.timeText);
        bestScoreText = findViewById(R.id.txtBestScoreGame);
        counterTextNew = findViewById(R.id.counterTextNew);
        counterTextEvent = findViewById(R.id.counterTextEvent);
        layout = findViewById(R.id.layoutGame);
        btnReiniciar = findViewById(R.id.btnReiniciar);
        btnClose = findViewById(R.id.btnFinish);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "starseed.ttf");
        counterText.setTypeface(typeface);
        timeText.setTypeface(typeface);
        nickText.setTypeface(typeface);
        bestScoreText.setTypeface(typeface);
        counterTextNew.setTypeface(typeface);
        counterTextEvent.setTypeface(typeface);

        Bundle extras = getIntent().getExtras();
        nick = extras.getString(Constants.EXTRA_NICK);
        idUser = extras.getString(Constants.EXTRA_ID);
        bestScore = extras.getString(Constants.EXTRA_BEST_SCORE);
        level = extras.getString(Constants.LEVEL);

        nickText.setText(nick);
        bestScoreText.setText("Best Score: "+bestScore);
        counterTextNew.setVisibility(View.INVISIBLE);
        counterTextEvent.setVisibility(View.INVISIBLE);

        btnReiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogRestart();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogClose();
            }
        });

    }

    private void showDialogClose(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_stop);
        dialog.setCancelable(false);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "starseed.ttf");

        TextView textTitle = dialog.findViewById(R.id.textCloseGameTitle);
        TextView textSubtitle = dialog.findViewById(R.id.textCloseGameSubtitle);
        textTitle.setTypeface(typeface);
        textSubtitle.setTypeface(typeface);

        FButton btnClose = dialog.findViewById(R.id.dialogButtonClose);
        btnClose.setTypeface(typeface);
        btnClose.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        counter = 0;
                        counterText.setText("0");
                        gameOver = false;
                        generatorDucksTimer.cancel();
                        deleteDucks();
                        timer.cancel();
                        dialog.dismiss();

                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            Log.d("TAG", "The interstitial wasn't loaded yet.");
                        }

                        Intent i = new Intent(GameActivity.this, MenuActivity.class);
                        finish();
                        startActivity(i);
                    }
                });

        FButton btnCancelar = dialog.findViewById(R.id.dialogButtonCancelClose);
        btnCancelar.setTypeface(typeface);
        btnCancelar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

        dialog.show();
    }

    private void showDialogRestart(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_restart);
        dialog.setCancelable(false);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "starseed.ttf");

        TextView textTitle = dialog.findViewById(R.id.textRestartGameTitle);
        TextView textSubtitle = dialog.findViewById(R.id.textRestartGameSubtitle);
        textTitle.setTypeface(typeface);
        textSubtitle.setTypeface(typeface);

        FButton btnRestart = dialog.findViewById(R.id.dialogButtonRestart);
        btnRestart.setTypeface(typeface);
        btnRestart.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        counter = 0;
                        counterText.setText("0");
                        gameOver = false;
                        generatorDucksTimer.cancel();
                        deleteDucks();
                        generateDucks();
                        timer.cancel();
                        initCountDown();
                        dialog.dismiss();
                    }
                });

        FButton btnCancelar = dialog.findViewById(R.id.dialogButtonCancel);
        btnCancelar.setTypeface(typeface);
        btnCancelar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

        dialog.show();
    }

    private void chargeUser() {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        player = documentSnapshot.toObject(User.class);
                    }
                });
    }

    private void actualizarPuntuacion(int puntosConseguidos) {
        if (player.getDucks() < puntosConseguidos) {
            player.setDucks(puntosConseguidos);
        }

        if (level.equals(Constants.LEVEL_HARD) && player.getDucksHard() < puntosConseguidos)
            player.setDucksHard(puntosConseguidos);
        else if (level.equals(Constants.LEVEL_MEDIUM) && player.getDucksMedium() < puntosConseguidos)
            player.setDucksMedium(puntosConseguidos);
        else if (level.equals(Constants.LEVEL_EASY) && player.getDucksEasy() < puntosConseguidos)
            player.setDucksEasy(puntosConseguidos);

        player.setGamesPlayed(player.getGamesPlayed()+1);

        db.collection("users")
                .document(uid)
                .set(player)
                .addOnSuccessListener(GameActivity.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(GameActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GameActivity.this, "Ups, something bad happened, we can't save your last game",Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void drawNewDuck(Duck duck){
        switch (duck.getType()){
            case 2:
                if (duck.isLeft())
                    duck.getView().setImageResource(R.drawable.duck_left_2);
                else
                    duck.getView().setImageResource(R.drawable.duck_2);
                break;
            case 3:
                if (duck.isLeft())
                    duck.getView().setImageResource(R.drawable.duck_left_3);
                else
                    duck.getView().setImageResource(R.drawable.duck_3);
                break;
            default:
                if (duck.isLeft())
                    duck.getView().setImageResource(R.drawable.duck_left_1);
                else
                    duck.getView().setImageResource(R.drawable.duck_1);
        }
    }

    private void drawHuntedDuck(Duck duck){
        switch (duck.getType()){
            case 2:
                if (duck.isLeft())
                    duck.getView().setImageResource(R.drawable.duck_hunted_2);
                else
                    duck.getView().setImageResource(R.drawable.duck_hunted_2);
                break;
            case 3:
                if (duck.isLeft())
                    duck.getView().setImageResource(R.drawable.duck_hunted_3);
                else
                    duck.getView().setImageResource(R.drawable.duck_hunted_3);
                break;
            default:
                if (duck.isLeft())
                    duck.getView().setImageResource(R.drawable.duck_hunted_1);
                else
                    duck.getView().setImageResource(R.drawable.duck_hunted_1);
        }
    }


}
