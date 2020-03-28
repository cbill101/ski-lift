package com.example.skilift;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
                openInfo();
            }
        });
    }

    //Open maps page
    private void openRideList() {
        Intent intent = new Intent(this, RideList.class);
        startActivity(intent);
    }

    //Open info page
    private void openInfo() {
        Intent intent = new Intent(this, Info.class);
        startActivity(intent);
    }
}
