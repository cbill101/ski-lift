package com.example.skilift;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public abstract class OnboardingCommon extends AppCompatActivity implements LoginLayout {
    private static final String TAG = "OnboardingCommon";
    private static final String BUNDLE_USERNAME = "Username";
    private static final String BUNDLE_PASSWORD = "Password";

    private EditText passwordInput;
    private TextInputLayout passwordLayout;
    private EditText usernameInput;
    private TextInputLayout usernameLayout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());

        usernameInput = findViewById(R.id.usernameInput);
        usernameLayout = findViewById(R.id.usernameInputLayout);
        passwordInput = findViewById(R.id.passwordInput);
        passwordLayout = findViewById(R.id.passwordInputLayout);

        mAuth = FirebaseAuth.getInstance();

        // better than onRestoreInstanceState looking at the lifecycle
        if(savedInstanceState != null) {
            usernameInput.setText(savedInstanceState.getString(BUNDLE_USERNAME));
            passwordInput.setText(savedInstanceState.getString(BUNDLE_PASSWORD));
        }

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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_USERNAME, usernameInput.getText().toString());
        outState.putString(BUNDLE_PASSWORD, passwordInput.getText().toString());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    /**
     * Verifies a password being:
     * - at least 8 chars
     * - has at least one letter, number and special character
     * @return true if valid password, false if not
     */
    public boolean correctPasswordForm() {
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
     * Reges pattern to check for a valid formed email.
     * @return true is the string is in correct form, false otherwise
     */
    public boolean correctEmailForm(){
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
     * Sign in to the account through Firebase (non Google).
     * @param email - the email to check.
     * @param password - the user's password to check.
     */
    public void signIn(String email, String password) {
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

    /**
     * Updates the UI depending on if a user is signed into the app or not through Firebase.
     *
     * @param user - the user signed in. This is null is no user is signed in.
     */
    public void updateUI(FirebaseUser user){
        if (user != null) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(User.class).getFirstName();
                    Toast.makeText(getApplicationContext(), "Welcome back, " + name + "!",
                            Toast.LENGTH_SHORT).show();
                    openUserType();
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
    public abstract int getLayoutID();

    public class MyTextWatcher implements TextWatcher {

        private View view;

        public MyTextWatcher(View view) {
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