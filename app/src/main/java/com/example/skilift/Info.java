package com.example.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Info extends AppCompatActivity {
    private static final String BUNDLE_NAME = "";
    private static final String BUNDLE_PHONE = "";
    private static final String BUNDLE_PRICE = "";
    private Button nextPageButton;
    private Button sendButton;
    private EditText nameInput;
    private EditText phoneInput;
    private EditText priceInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        nextPageButton = findViewById(R.id.finishedButton);
        sendButton = findViewById(R.id.sendButton);
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        priceInput = findViewById(R.id.priceInput);
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWait();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndersonStuff();
            }
        });
    }

    //For now goes to next page
    private void openWait() {
        Intent intent = new Intent(this, Wait.class);
        startActivity(intent);
    }

    //Anderson put the stuff you need to have happen when the button is pressed here
    private void AndersonStuff() {
        // Get text from fields
        String name = nameInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String price = priceInput.getText().toString();

        // Save provider info into a hashmap
        HashMap<String, Object> provInfoMap = new HashMap<>();
        provInfoMap.put("name", name);
        provInfoMap.put("phone", phone);
        provInfoMap.put("price", price);

        // Check for empty fields
        if(name.isEmpty() || phone.isEmpty() || price.isEmpty()){
            Toast.makeText(Info.this, "Please enter info in all fields", Toast.LENGTH_SHORT).show();
        } else {
            // Save to db
            FirebaseDatabase.getInstance().getReference()
                    .child("Providers")
                    .child(provInfoMap.get("name").toString())
                    .setValue(provInfoMap);
            Toast.makeText(Info.this, "Info saved!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_NAME, nameInput.getText().toString());
        outState.putString(BUNDLE_PHONE, phoneInput.getText().toString());
        outState.putString(BUNDLE_PRICE, priceInput.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        nameInput.setText(savedInstanceState.getString(BUNDLE_NAME));
        phoneInput.setText(savedInstanceState.getString(BUNDLE_PHONE));
        priceInput.setText(savedInstanceState.getString(BUNDLE_PRICE));
    }
}
