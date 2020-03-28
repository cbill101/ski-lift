package com.example.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthMultiFactorException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.MultiFactorResolver;
import java.util.regex.*;


public class LoginMenu extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String BUNDLE_USERNAME = "";
    private static final String BUNDLE_PASSWORD = "";
    private Button loginButton;
    private Button createAccountButton;
    private EditText usernameInput;
    private EditText passwordInput;
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
        createAccountButton = findViewById(R.id.createAccountButton);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
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

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email +" Correct Form:" + correctEmailForm(email));
        if(correctEmailForm(email) && correctPassword(password)){
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
        }else{
            Toast.makeText(getApplicationContext(), "Email or password not correct style.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "Sign In :" + email +" Correct Form:" + correctEmailForm(email));
        if(correctEmailForm(email) && correctPassword(password)) {

            //showProgressBar();

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
        }else{
            Toast.makeText(getApplicationContext(), "Email or password not correct style.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private boolean correctEmailForm(String email){
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }

    private boolean correctPassword(String password){
        return true;
    }


    private void updateUI(FirebaseUser user){
        if (user == null) {
            Toast.makeText(getApplicationContext(), "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Authentication success.",
                    Toast.LENGTH_SHORT).show();
            openUserType();
        }
    }


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

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        usernameInput.setText(savedInstanceState.getString(BUNDLE_USERNAME));
        passwordInput.setText(savedInstanceState.getString(BUNDLE_PASSWORD));
    }
}
