package com.example.skilift.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.skilift.R;

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
                openMapRequester();
            }
        });
        providerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapProvider();
            }
        });
    }

    //Open maps page
    private void openMapRequester() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("UserType", "Requester");
        startActivity(intent);
    }

    //Open info page
    private void openMapProvider() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("UserType", "Provider");
        startActivity(intent);
    }
}
