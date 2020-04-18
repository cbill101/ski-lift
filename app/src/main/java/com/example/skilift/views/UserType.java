package com.example.skilift.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

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
