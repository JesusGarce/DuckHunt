package es.jesusgarce.duckhunt.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import es.jesusgarce.duckhunt.R;
import es.jesusgarce.duckhunt.models.User;
import info.hoang8f.widget.FButton;

import android.app.Dialog;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private EditText etEmail, etPassword;
    private FButton btnLogin, btnRegistro, btnResetPassword;
    private ScrollView formLogin;
    private TextView pbLogin;
    FirebaseAuth firebaseAuth;
    String email, password;
    boolean tryLogin = false;
    private AdView mAdView;
    LoginButton loginButton;
    CallbackManager callbackManager;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        setContentView(R.layout.activity_login);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        etEmail = findViewById(R.id.editEmail);
        etPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.buttonRegistro);
        btnRegistro = findViewById(R.id.buttonRegistroLogin);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        formLogin = findViewById(R.id.formLogin);
        pbLogin = findViewById(R.id.loadingTextLogin);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "starseed.ttf");
        etEmail.setTypeface(typeface);
        etPassword.setTypeface(typeface);
        btnLogin.setTypeface(typeface);
        btnRegistro.setTypeface(typeface);
        btnResetPassword.setTypeface(typeface);
        pbLogin.setTypeface(typeface);

        btnRegistro.setButtonColor(getResources().getColor(R.color.fbutton_secondary_color));

        firebaseAuth = FirebaseAuth.getInstance();

        MobileAds.initialize(this, "ca-app-pub-3409312019014737/8093290780");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        changeLoginFormVisibility(true);
        events();

        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                changeLoginFormVisibility(false);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });


        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
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

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogResetPassword();
            }
        });

    }

    private void showDialogResetPassword(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_reset_password);
        dialog.setCancelable(false);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "starseed.ttf");

        TextView textTitle = dialog.findViewById(R.id.textResetPasswordTitle);
        EditText emailResetPassword = dialog.findViewById(R.id.resetPasswordEmail);
        textTitle.setTypeface(typeface);
        emailResetPassword.setTypeface(typeface);

        FButton btnReset = dialog.findViewById(R.id.dialogResetPassword);
        btnReset.setTypeface(typeface);
        btnReset.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = emailResetPassword.getText().toString();

                        firebaseAuth.sendPasswordResetEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(dialog.getContext(), "Check your e-mail to reset your password", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(dialog.getContext(), "Ups! Your email isn't correct. Try again", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                });

        FButton btnCancelar = dialog.findViewById(R.id.dialogResetCancel);
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
                LoginManager.getInstance().logOut();
                changeLoginFormVisibility(true);
            }
        } else {
            changeLoginFormVisibility(true);
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

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            updateUI(null);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("JGS", "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            createUserFirebase(user);
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    private void createUserFirebase(FirebaseUser user){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        User newUser = new User(user.getDisplayName(), 0, 0, 0, 0);
        db.collection("users")
                .document(user.getUid())
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateUI(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "We can't create your profile, try manually", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("JGS", "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            createUserFirebase(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("JGS", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            changeLoginFormVisibility(true);
                            updateUI(null);
                        }
                    }
                });
    }
}
