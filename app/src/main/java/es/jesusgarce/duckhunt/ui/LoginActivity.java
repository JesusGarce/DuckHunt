package es.jesusgarce.duckhunt.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import es.jesusgarce.duckhunt.R;
import info.hoang8f.widget.FButton;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private FButton btnLogin, btnRegistro;
    private ScrollView formLogin;
    private TextView pbLogin;
    FirebaseAuth firebaseAuth;
    String email, password;
    boolean tryLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.editEmail);
        etPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.buttonRegistro);
        btnRegistro = findViewById(R.id.buttonRegistroLogin);
        formLogin = findViewById(R.id.formLogin);
        pbLogin = findViewById(R.id.loadingTextLogin);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "starseed.ttf");
        etEmail.setTypeface(typeface);
        etPassword.setTypeface(typeface);
        btnLogin.setTypeface(typeface);
        btnRegistro.setTypeface(typeface);
        pbLogin.setTypeface(typeface);

        btnRegistro.setButtonColor(getResources().getColor(R.color.fbutton_secondary_color));

        firebaseAuth = FirebaseAuth.getInstance();

        changeLoginFormVisibility(true);
        events();
    }

    private void events() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();

                if (password.isEmpty()) {
                    etPassword.setError("You must write a password");
                } else if (email.isEmpty()) {
                    etEmail.setError("You must write an e-mail");
                } else {
                    changeLoginFormVisibility(false);
                    loginUser();
                }

            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(i);
            }
        });

    }

    private void loginUser() {
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        tryLogin = true;
                        if (task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w("JGS", "LogInError: "+task.getException());
                            Toast.makeText(LoginActivity.this, "Ups! Your password isn't correct. Check it", Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null){
            if (user.isEmailVerified()) {
                Intent i = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(i);
            }
            else {
                user.sendEmailVerification();
                Toast.makeText(LoginActivity.this, "You have to verify your e-mail. We have sent another verification. Please check your e-mail", Toast.LENGTH_LONG).show();
                changeLoginFormVisibility(true);
            }
        } else {
            changeLoginFormVisibility(true);
            etEmail.requestFocus();
        }
    }

    private void changeLoginFormVisibility(boolean showForm) {
        pbLogin.setVisibility(showForm ? View.GONE : View.VISIBLE);
        formLogin.setVisibility(showForm ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }
}
