package es.jesusgarce.duckhunt.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import es.jesusgarce.duckhunt.R;
import es.jesusgarce.duckhunt.models.User;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class RegistroActivity extends AppCompatActivity {
    EditText etName, etEmail, etPass;
    Button btnRegistro;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    TextView loadingRegistro;
    ScrollView formRegistro;
    private AdView mAdView;

    String name, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etName = findViewById(R.id.inputNick);
        etEmail = findViewById(R.id.inputEmail);
        etPass = findViewById(R.id.inputPass);

        btnRegistro = findViewById(R.id.buttonSignIn);
        loadingRegistro = findViewById(R.id.loadingTextRegistro);
        formRegistro = findViewById(R.id.formRegistro);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "starseed.ttf");
        etName.setTypeface(typeface);
        etEmail.setTypeface(typeface);
        etPass.setTypeface(typeface);
        btnRegistro.setTypeface(typeface);
        loadingRegistro.setTypeface(typeface);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        MobileAds.initialize(this, "ca-app-pub-3409312019014737/8093290780");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        changeRegistroFormVisibility(true);
        events();
    }

    private void events() {
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etName.getText().toString();
                password = etPass.getText().toString();
                email = etEmail.getText().toString();

                if (name.isEmpty()){
                    etName.setError("You must write a username");
                } else if (password.isEmpty()) {
                    etPass.setError("You must write a password");
                } else if (email.isEmpty()) {
                    etEmail.setError("You must write an e-mail");
                } else {
                    checkNick();
                }
            }
        });
    }

    private void createUser() {
        changeRegistroFormVisibility(false);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(RegistroActivity.this, "Ups! Something bad happened... Try later", Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });

    }

    private void checkNick() {
        db.collection("users").whereEqualTo("nick",name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0){
                            etName.setError("This username isn't available");
                        } else {
                            createUser();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null){

            User newUser = new User(name, 0, 0, 0, 0);

            db.collection("users")
                    .document(user.getUid())
                    .set(newUser)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            firebaseAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegistroActivity.this, "Registered successfully. Please check your e-mail to verify your account.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                            finish();
                            Intent i = new Intent(RegistroActivity.this, LoginActivity.class);
                            startActivity(i);
                        }
                    });
        } else {
            changeRegistroFormVisibility(true);
            etPass.setError("Username, E-mail or Password wrong");
            etPass.requestFocus();
        }
    }

    private void changeRegistroFormVisibility(boolean showForm) {
        loadingRegistro.setVisibility(showForm ? View.GONE : View.VISIBLE);
        formRegistro.setVisibility(showForm ? View.VISIBLE : View.GONE);
    }
}
