package com.example.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthMultiFactorException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.MultiFactorResolver;
import java.util.regex.*;


public class LoginMenu extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String BUNDLE_USERNAME = "";
    private static final String BUNDLE_PASSWORD = "";

    private static int RC_GSO = 0;

    private Button loginButton;
    private Button createAccountButton;
    private SignInButton gsoButton;
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;

    private EditText usernameInput;
    private EditText passwordInput;
    private GoogleSignInClient mGoogleSignInClient;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);
        loginButton = findViewById(R.id.loginButton);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        usernameLayout = findViewById(R.id.usernameInputLayout);
        passwordLayout = findViewById(R.id.passwordInputLayout);
        createAccountButton = findViewById(R.id.createAccountButton);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // better than onRestoreInstanceState looking at the lifecycle
        if(savedInstanceState != null) {
            usernameInput.setText(savedInstanceState.getString(BUNDLE_USERNAME));
            passwordInput.setText(savedInstanceState.getString(BUNDLE_PASSWORD));
        }

        // [START initialize_auth]
        // Initialize Firebase Auth

        mAuth = FirebaseAuth.getInstance();

        // Else, sign in options! Google:
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        gsoButton = findViewById(R.id.googleLoginButton);
        gsoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });

        usernameInput.addTextChangedListener(new MyTextWatcher(usernameInput));
        passwordInput.addTextChangedListener(new MyTextWatcher(passwordInput));

        usernameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    correctEmailForm();
                }
            }
        });

        passwordInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    correctPasswordForm();
                }
            }
        });

        // [END initialize_auth]
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(usernameInput.getText().toString(),passwordInput.getText().toString());
            }
        });
        createAccountButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                createAccount(usernameInput.getText().toString(),passwordInput.getText().toString());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    /**
     * Creates an account with the given email and password fields entered by user.
     * @param email - the email to create an account with.
     * @param password - the password to use for the account in question
     */
    private void createAccount(String email, String password) {
        boolean validPass = correctPasswordForm();
        boolean validEmail = correctEmailForm();

        Log.d(TAG, "createAccount:" + email +" Correct Form:" + validEmail);

        if(validEmail && validPass){
            // [START create_user_with_email]
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                updateUI(null);
                            }

                        }
                    });
            // [END create_user_with_email]
        }
        else {
            usernameLayout.setError(getString(R.string.err_msg_incorrect_any));
            requestFocus(usernameInput);
        }
    }

    /**
     * Sign in to the account through Firebase (non Google).
     * @param email - the email to check.
     * @param password - the user's password to check.
     */
    private void signIn(String email, String password) {
        boolean validPass = correctPasswordForm();
        boolean validEmail = correctEmailForm();

        Log.d(TAG, "Sign In: " + email +" Correct Form: " + validEmail);
        Log.d(TAG, "Correct Password Form: " + validPass);

        if(validEmail && validPass) {
            // [START sign_in_with_email]
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                passwordLayout.setError(getString(R.string.err_msg_incorrect_any));
                                requestFocus(passwordLayout);
                                updateUI(null);
                                // [START_EXCLUDE]
                                //checkForMultiFactorFailure(task.getException());
                                // [END_EXCLUDE]
                            }

                            // [START_EXCLUDE]
//                            if (!task.isSuccessful()) {
//                                mStatusTextView.setText(R.string.auth_failed);
//                            }
//                            hideProgressBar();
                            // [END_EXCLUDE]
                        }
                    });
            // [END sign_in_with_email]
        }
        else {
            passwordLayout.setError(getString(R.string.err_msg_incorrect_any));
            requestFocus(passwordLayout);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GSO) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    /**
     * Google SSO helper method to start signing in through Google.
     */
    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GSO);
    }

    /**
     * Sign in to Ski Lift using a Google Account + Firebase.
     * @param acct - the Google Account instance to use
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.loginConstraintLayout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * Reges pattern to check for a valid formed email.
     * @return true is the string is in correct form, false otherwise
     */
    private boolean correctEmailForm(){
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        String text = usernameInput.getText().toString().trim();

        if (!text.matches(regex)) {
            usernameLayout.setError(getString(R.string.err_msg_email));
            return false;
        } else {
            usernameLayout.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * Verifies a password being:
     * - at least 8 chars
     * - has at least one letter, number and special character
     * @return true if valid password, false if not
     */
    private boolean correctPasswordForm() {
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
        String text = passwordInput.getText().toString().trim();

        if (!text.matches(regex)) {
            passwordLayout.setError(getString(R.string.err_msg_password));
            return false;
        } else {
            passwordLayout.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * Updates the UI depending on if a user is signed into the app or not through Firebase.
     *
     * @param user - the user signed in. This is null is no user is signed in.
     */
    private void updateUI(FirebaseUser user){
        if (user != null) {
            Toast.makeText(getApplicationContext(), "Welcome back, " + user.getEmail(),
                    Toast.LENGTH_SHORT).show();
            openUserType();
            finish();
        }
    }

    /**
     * Starts the userType activity, used in updateUI().
     */
    private void openUserType() {
        Intent intent = new Intent(this, UserType.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_USERNAME, usernameInput.getText().toString());
        outState.putString(BUNDLE_PASSWORD, passwordInput.getText().toString());
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.usernameInput:
                    if(usernameInput.getText().toString().length() != 0)
                        correctEmailForm();
                    break;
                case R.id.passwordInput:
                    if(passwordInput.getText().toString().length() != 0)
                        correctPasswordForm();
                    break;
            }
        }
    }
}
