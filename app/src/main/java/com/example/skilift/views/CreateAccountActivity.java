package com.example.skilift.views;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.skilift.R;
import com.example.skilift.models.User;
import com.example.skilift.viewmodels.OnboardingVM;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
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

    private OnboardingVM onboardViewModel;

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

        onboardViewModel = ViewModelProviders.of(this).get(OnboardingVM.class);

        // better than onRestoreInstanceState looking at the lifecycle
        if(savedInstanceState != null) {
            usernameInput.setText(savedInstanceState.getString(BUNDLE_USERNAME));
            passwordInput.setText(savedInstanceState.getString(BUNDLE_PASSWORD));
            nameInput.setText(savedInstanceState.getString(BUNDLE_NAME));
            phoneInput.setText(savedInstanceState.getString(BUNDLE_PHONE));
            confirmPassInput.setText(savedInstanceState.getString(BUNDLE_CONFIRM_PASS));
        }

        phoneInput.addTextChangedListener(new MyTextWatcher(phoneInput));
        nameInput.addTextChangedListener(new MyTextWatcher(nameInput));
        confirmPassInput.addTextChangedListener(new MyTextWatcher(confirmPassInput));

        createAccountButton = findViewById(R.id.createAccountButton);

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

        Log.d(TAG, "createAccount: Email: " + email +" Correct Form:" + validEmail);
        Log.d(TAG, "createAccount:" + " Password valid: " + validPass);
        Log.d(TAG, "createAccount:" + " Passwords match: " + passMatch);

        if(validEmail && validPass && validName && validPhone && passMatch){
            onboardViewModel.createAccount(email, password).observe(this, fbUser -> {
                addUserInfo(fbUser);
            });
        }
    }

    /**
     * Adds user info, called when auth is successful (meaning all verified so no need to re check.
     */
    private void addUserInfo(FirebaseUser user) {
        String name = nameInput.getText().toString().trim();
        String phone = ccp.getFormattedFullNumber();
        String email = usernameInput.getText().toString().trim();

        User newUser = new User(user.getUid(), name, phone, email);
        onboardViewModel.saveUser(newUser);
        updateUI(user);
    }

    private boolean validPhone() {
        if(ccp.isValidFullNumber()) {
            phoneLayout.setError(null);
            return true;
        }

        phoneLayout.setError("Phone number not valid.");
        requestFocus(phoneLayout);
        return false;
    }

    private boolean validName() {
        String text = nameInput.getText().toString().trim();

        if(!text.isEmpty()) {
            nameLayout.setError(null);
            return true;
        }

        nameLayout.setError("Name can't be empty.");
        requestFocus(nameInput);
        return false;
    }

    private boolean confirmPass() {
        if(confirmPassInput.getText().toString().trim().equals(passwordInput.getText().toString().trim())) {
            confirmPassLayout.setError(null);
            return true;
        }

        confirmPassLayout.setError("Passwords don't match.");
        requestFocus(confirmPassInput);
        return false;
    }

    private class MyTextWatcher implements TextWatcher {

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
                case R.id.confirmPassInput:
                    if(confirmPassInput.getText().toString().length() != 0)
                        confirmPass();
                    break;
                case R.id.phoneInput:
                    if(phoneInput.getText().toString().length() != 0)
                        validPhone();
                    break;
                case R.id.nameInput:
                    if(nameInput.getText().toString().length() != 0)
                        validName();
                    break;
            }
        }
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_create_account;
    }
}
