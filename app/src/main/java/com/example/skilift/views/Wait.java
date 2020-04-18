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

import com.example.skilift.R;

public class Wait extends AppCompatActivity {
    private Button recievedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        recievedButton = findViewById(R.id.finishedButton);
        recievedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openMaps();
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
    private void openMaps() {
        Intent intent = new Intent(this, UserType.class);
        startActivity(intent);
    }
}
