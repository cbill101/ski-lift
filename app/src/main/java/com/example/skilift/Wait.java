package com.example.skilift;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Wait extends AppCompatActivity {
    private Button recievedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        recievedButton = findViewById(R.id.finishedButton);
        recievedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openMaps();
            }
        });
    }

    //Open maps page
    private void openMaps() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
