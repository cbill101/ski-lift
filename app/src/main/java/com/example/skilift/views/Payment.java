package com.example.skilift.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.skilift.R;
import com.example.skilift.views.Communicate;

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
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_context_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.account:
                startActivity(new Intent(getApplicationContext(), AccountPage.class));
                return true;
            case R.id.settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.chats:
                startActivity(new Intent(getApplicationContext(), ChatHistoryActivity.class));
                return true;
            case R.id.help:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //For now goes to next page
    private void openCommunicate() {
        Intent intent = new Intent(this, Communicate.class);
        startActivity(intent);
    }
}
