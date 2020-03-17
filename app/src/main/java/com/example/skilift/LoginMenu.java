package com.example.skilift;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class LoginMenu extends AppCompatActivity {
    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);

        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
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
}
