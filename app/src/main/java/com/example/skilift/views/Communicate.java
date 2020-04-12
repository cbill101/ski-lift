package com.example.skilift.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.skilift.R;

public class Communicate extends AppCompatActivity {
    private Button finishedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicate);
        finishedButton = findViewById(R.id.finishedButton);
        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openMainMenu();
            }
        });
    }

    //Open maps page
    private void openMainMenu() {
        Intent intent = new Intent(this, UserType.class);
        startActivity(intent);
    }
}
