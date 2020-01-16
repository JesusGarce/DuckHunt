package es.jesusgarce.duckhunt.ui;

import androidx.appcompat.app.AppCompatActivity;
import es.jesusgarce.duckhunt.R;
import es.jesusgarce.duckhunt.common.Constants;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class GameActivity extends AppCompatActivity {
    ImageView duckLeft;
    ImageView duckRight;
    TextView nickText;
    TextView counterText;
    TextView timeText;
    int counter = 0;
    int widthScreen;
    int heightScreen;
    Random random;
    boolean gameOver = false;
    String idUser;
    String nick;
    FirebaseFirestore db;
    ObjectAnimator animationLeft;
    ObjectAnimator animationRight;
    int duckChosed = 1;
    boolean isLeft = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        db = FirebaseFirestore.getInstance();

        initViewComponents();
        events();
        initScreen();
        moveDuckLeft();
        moveDuckRight();
        initCountDown();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initCountDown() {
        new CountDownTimer(60000, 1000){
            @Override
            public void onTick(long l) {
                long remainingSeconds = l / 1000;
                timeText.setText(remainingSeconds + "s");
            }

            @Override
            public void onFinish() {
                timeText.setText("0s");
                gameOver = true;
                saveResultOnFirestore();
                showDialogGameOver();
            }
        }.start();
    }

    private void saveResultOnFirestore() {
        db.collection("users")
                .document(idUser)
                .update("ducks", counter);

    }

    private void showDialogGameOver() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_end);
        dialog.setTitle("GAME OVER");
        dialog.setCancelable(false);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "pixel.ttf");

        TextView textGameOver = dialog.findViewById(R.id.textGameOver);
        TextView textResult = dialog.findViewById(R.id.textResultGO);
        textResult.setText("You have hunted "+counter + " ducks");
        textResult.setTypeface(typeface);
        textGameOver.setTypeface(typeface);

        Button dialogReiniciar = dialog.findViewById(R.id.dialogButtonReiniciar);
        dialogReiniciar.setTypeface(typeface);
        dialogReiniciar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        counter = 0;
                        counterText.setText("0");
                        gameOver = false;
                        initCountDown();
                        dialog.dismiss();
                    }
        });

        Button dialogVerRanking = dialog.findViewById(R.id.dialogButtonRanking);
        dialogVerRanking.setTypeface(typeface);
        dialogVerRanking.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Intent i = new Intent(GameActivity.this, RankingActivity.class);
                        startActivity(i);
                        finish();
                    }
                });

        dialog.show();
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
        duckLeft = findViewById(R.id.duckLeft);
        duckRight = findViewById(R.id.duckRight);
        nickText = findViewById(R.id.nickText);
        counterText = findViewById(R.id.counterText);
        timeText = findViewById(R.id.timeText);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "pixel.ttf");
        counterText.setTypeface(typeface);
        timeText.setTypeface(typeface);
        nickText.setTypeface(typeface);

        Bundle extras = getIntent().getExtras();
        nick = extras.getString(Constants.EXTRA_NICK);
        idUser = extras.getString(Constants.EXTRA_ID);
        nickText.setText(nick);
    }

    private void events() {
        duckLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!gameOver) {
                    counter++;
                    counterText.setText(String.valueOf(counter));
                    isLeft = true;

                    drawHuntedDuck(isLeft);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animationLeft.end();
                            duckChosed = random.nextInt((4 - 1) + 1);
                            drawNewDuck(isLeft);
                            animationLeft.start();
                        }
                    }, 50);
                }
            }
        });

        duckRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!gameOver) {
                    counter++;
                    counterText.setText(String.valueOf(counter));
                    isLeft = false;

                    drawHuntedDuck(false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animationRight.end();
                            duckChosed = random.nextInt((4 - 1) + 1);
                            drawNewDuck(isLeft);
                            animationRight.start();
                        }
                    }, 50);
                }
            }
        });
    }

    private void moveDuckLeft(){
        int maxWidth = widthScreen;

        duckLeft.setX(-duckLeft.getWidth());
        animationLeft = ObjectAnimator.ofFloat(duckLeft, "translationX",  maxWidth, -220f);
        animationLeft.setDuration(981);
        animationLeft.setRepeatCount(ValueAnimator.INFINITE);
        animationLeft.setRepeatMode(ValueAnimator.RESTART);
        animationLeft.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                int min = 0;
                int maxHeight = heightScreen - duckLeft.getHeight();
                int randomHeight = random.nextInt((maxHeight - min) + 1);
                duckLeft.setY(randomHeight);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                int min = 0;
                int maxHeight = heightScreen - duckLeft.getHeight();
                int randomHeight = random.nextInt((maxHeight - min) + 1);
                duckLeft.setY(randomHeight);
            }
        });
        animationLeft.start();

    }

    private void moveDuckRight(){
        int maxWidth = widthScreen + duckRight.getWidth();

        duckRight.setX(+duckRight.getWidth());
        animationRight = ObjectAnimator.ofFloat(duckRight, "translationX",  -90f, maxWidth);
        animationRight.setDuration(1016);
        animationRight.setRepeatCount(ValueAnimator.INFINITE);
        animationRight.setRepeatMode(ValueAnimator.RESTART);
        animationRight.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                int min = 0;
                int maxHeight = heightScreen - duckRight.getHeight();
                int randomHeight = random.nextInt((maxHeight - min) + 1);
                duckRight.setY(randomHeight);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                int min = 0;
                int maxHeight = heightScreen - duckRight.getHeight();
                int randomHeight = random.nextInt((maxHeight - min) + 1);
                duckRight.setY(randomHeight);
            }
        });
        animationRight.start();

    }

    private void drawNewDuck(boolean isLeft){
        switch (duckChosed){
            case 2:
                if (isLeft)
                    duckLeft.setImageResource(R.drawable.duck_left_2);
                else
                    duckRight.setImageResource(R.drawable.duck_2);
                break;
            case 3:
                if (isLeft)
                    duckLeft.setImageResource(R.drawable.duck_left_3);
                else
                    duckRight.setImageResource(R.drawable.duck_3);
                break;
            default:
                if (isLeft)
                    duckLeft.setImageResource(R.drawable.duck_left_1);
                else
                    duckRight.setImageResource(R.drawable.duck_1);
        }
    }

    private void drawHuntedDuck(boolean isLeft){
        switch (duckChosed){
            case 2:
                if (isLeft)
                    duckLeft.setImageResource(R.drawable.duck_hunted_2);
                else
                    duckRight.setImageResource(R.drawable.duck_hunted_2);
                break;
            case 3:
                if (isLeft)
                    duckLeft.setImageResource(R.drawable.duck_hunted_3);
                else
                    duckRight.setImageResource(R.drawable.duck_hunted_3);
                break;
            default:
                if (isLeft)
                    duckLeft.setImageResource(R.drawable.duck_hunted_1);
                else
                    duckRight.setImageResource(R.drawable.duck_hunted_1);
        }
    }

}
