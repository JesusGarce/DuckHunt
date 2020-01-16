package es.jesusgarce.duckhunt.ui;

import androidx.appcompat.app.AppCompatActivity;
import es.jesusgarce.duckhunt.R;
import es.jesusgarce.duckhunt.common.Constants;
import es.jesusgarce.duckhunt.models.User;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    EditText inputNick;
    Button btnStart;
    Button btnRanking;
    String nick;
    FirebaseFirestore db;
    Dialog loadingDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        db = FirebaseFirestore.getInstance();

        inputNick = findViewById(R.id.inputNick);
        btnStart = findViewById(R.id.buttonStart);
        btnRanking = findViewById(R.id.buttonRanking);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "pixel.ttf");
        inputNick.setTypeface(typeface);
        btnStart.setTypeface(typeface);
        btnRanking.setTypeface(typeface);

        btnStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                nick = inputNick.getText().toString();

                if (nick.isEmpty()){
                    inputNick.setError("You must write a nickname");
                } else if (nick.length() < 3) {
                    inputNick.setError("It's too short, you must use more than 3 characters");
                } else if (nick.length() > 15) {
                    inputNick.setError("It's too long, you must use less than 15 characters");
                }

                else {
                    loadingDialog = new Dialog(context);
                    loadingDialog.setContentView(R.layout.dialog_loading);

                    TextView loading = loadingDialog.findViewById(R.id.loadingText);
                    Typeface typeface = Typeface.createFromAsset(getAssets(), "pixel.ttf");
                    loading.setTypeface(typeface);

                    loadingDialog.setCancelable(false);
                    loadingDialog.show();
                    addNickAndStart();
                }
            }
        });

        btnRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RankingActivity.class);
                startActivity(i);
            }
        });
    }

    private void addNickAndStart() {
        db.collection("users").whereEqualTo("nick",nick)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0){
                            inputNick.setError("This nickname isn't available");
                        } else {
                            addNickToFirestore();
                        }
                    }
                });
    }

    private void addNickToFirestore() {
        db.collection("users")
                .add(new User(nick,0))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent i = new Intent(LoginActivity.this, GameActivity.class);
                        i.putExtra(Constants.EXTRA_NICK,nick);
                        i.putExtra(Constants.EXTRA_ID, documentReference.getId());
                        loadingDialog.cancel();
                        startActivity(i);
                    }
                });
    }
}
