package com.example.skilift;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserType extends AppCompatActivity {
    private Button requesterButton;
    private Button providerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);

        requesterButton = findViewById(R.id.requesterButton);
        providerButton = findViewById(R.id.providerButton);
        requesterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRideList();
            }
        });
        providerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });
    }

    //Open maps page
    private void openRideList() {
        Intent intent = new Intent(this, RideList.class);
        startActivity(intent);
    }

    //Open info page
    private void openMap() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
