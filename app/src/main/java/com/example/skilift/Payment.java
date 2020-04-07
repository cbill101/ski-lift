package com.example.skilift;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//Evan do your stuff here
public class Payment extends AppCompatActivity {
    private Button nextPageButton;
    private EditText CC;
    private EditText date;
    private EditText SC;
    private TextView priceTextView;
    private String providerUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Intent intent = getIntent();
        double price = intent.getDoubleExtra("price",5.0);
        providerUID = intent.getStringExtra("providerUID");
        CC = findViewById(R.id.editTextCC);
        date = findViewById(R.id.editTextDate);
        SC = findViewById(R.id.editTextSC);
        priceTextView = findViewById(R.id.priceTextView);
        priceTextView.append("Price: $"+String.valueOf(price));

        nextPageButton = findViewById(R.id.finishedButton);
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCommunicate();
            }
        });
    }

    //For now goes to next page
    private void openCommunicate() {
        Intent intent = new Intent(this, Communicate.class);
        startActivity(intent);
    }
}
