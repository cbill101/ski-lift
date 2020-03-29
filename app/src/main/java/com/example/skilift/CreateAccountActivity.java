package com.example.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseError;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;

public class CreateAccountActivity extends OnboardingCommon {

    private static final String TAG = "CreateAccountActivity";

    private static final String BUNDLE_NAME = "Name";
    private static final String BUNDLE_PHONE = "Phone";
    private static final String BUNDLE_CONFIRM_PASS = "ConfirmPass";
    private static final String BUNDLE_USERNAME = "Username";
    private static final String BUNDLE_PASSWORD = "Password";

    private EditText confirmPassInput;
    private EditText nameInput;
    private EditText phoneInput;
    private EditText passwordInput;
    private EditText usernameInput;

    private TextInputLayout passwordLayout;
    private TextInputLayout usernameLayout;
    private TextInputLayout confirmPassLayout;
    private TextInputLayout nameLayout;
    private TextInputLayout phoneLayout;

    private Button createAccountButton;

    private CountryCodePicker ccp;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        confirmPassInput = findViewById(R.id.confirmPassInput);
        confirmPassLayout = findViewById(R.id.confirmPassInputLayout);
        nameInput = findViewById(R.id.nameInput);
        nameLayout = findViewById(R.id.nameInputLayout);
        phoneInput = findViewById(R.id.phoneInput);
        phoneLayout = findViewById(R.id.phoneInputLayout);
        usernameInput = findViewById(R.id.usernameInput);
        usernameLayout = findViewById(R.id.usernameInputLayout);
        passwordInput = findViewById(R.id.passwordInput);
        passwordLayout = findViewById(R.id.passwordInputLayout);

        // better than onRestoreInstanceState looking at the lifecycle
        if(savedInstanceState != null) {
            usernameInput.setText(savedInstanceState.getString(BUNDLE_USERNAME));
            passwordInput.setText(savedInstanceState.getString(BUNDLE_PASSWORD));
            nameInput.setText(savedInstanceState.getString(BUNDLE_NAME));
            phoneInput.setText(savedInstanceState.getString(BUNDLE_PHONE));
            confirmPassInput.setText(savedInstanceState.getString(BUNDLE_CONFIRM_PASS));
        }

        createAccountButton = findViewById(R.id.createAccountButton);

        createAccountButton.setBackground(getDrawable(R.drawable.button_default));

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phoneInput);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_USERNAME, usernameInput.getText().toString());
        outState.putString(BUNDLE_PASSWORD, passwordInput.getText().toString());
        outState.putString(BUNDLE_NAME, nameInput.getText().toString());
        outState.putString(BUNDLE_PHONE, ccp.getFullNumber());
        outState.putString(BUNDLE_CONFIRM_PASS, confirmPassInput.getText().toString());
    }

    /**
     * Creates an account with the given email and password fields entered by user.
     */
    public void createAccount() {
        boolean validPass = correctPasswordForm();
        boolean validEmail = correctEmailForm();
        boolean passMatch = confirmPass();
        boolean validName = validName();
        boolean validPhone = validPhone();

        String email = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        mAuth = FirebaseAuth.getInstance();

        Log.d(TAG, "createAccount: Email: " + email +" Correct Form:" + validEmail);
        Log.d(TAG, "createAccount:" + " Password valid: " + validPass);;
        Log.d(TAG, "createAccount:" + " Passwords match: " + passMatch);;

        if(validEmail && validPass && validName && validPhone && passMatch){
            // [START create_user_with_email]
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                addUserInfo();
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
    }

    /**
     * Adds user info, called when auth is successful (meaning all verified so no need to re check.
     */
    private void addUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String email = usernameInput.getText().toString().trim();

        // Save user info into a hashmap
        HashMap<String, Object> userInfoMap = new HashMap<>();
        userInfoMap.put("Name", name);
        userInfoMap.put("Phone", phone);
        userInfoMap.put("Email", email);

        reference.child(mAuth.getCurrentUser().getUid())
                    .setValue(userInfoMap);
        Log.d(TAG, "addUserInfo: user info added successfully to DB.");
    }

    private boolean validPhone() {
        if(ccp.isValidFullNumber())
            return true;

        phoneLayout.setError("Phone number not valid.");
        requestFocus(phoneLayout);
        return false;
    }

    private boolean validName() {
        String text = nameInput.getText().toString().trim();

        if(!text.isEmpty())
            return true;

        nameLayout.setError("Name can't be empty.");
        requestFocus(nameInput);
        return false;
    }

    private boolean confirmPass() {
        if(confirmPassInput.getText().toString().trim().equals(passwordInput.getText().toString().trim())) {
            return true;
        }

        confirmPassLayout.setError("Passwords don't match.");
        requestFocus(confirmPassInput);
        return false;
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_create_account;
    }
}
