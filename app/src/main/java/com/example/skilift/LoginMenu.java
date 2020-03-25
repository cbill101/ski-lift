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

public class LoginMenu extends AppCompatActivity {
    private static final String BUNDLE_USERNAME = "username";
    private static final String BUNDLE_PASSWORD = "password";
    private Button loginButton;
    private Button registerButton;
    private EditText usernameInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);

        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserType();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
            public void onClick(View v) {
                openUserType();
            }
        });
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
