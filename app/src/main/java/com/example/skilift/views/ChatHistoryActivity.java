package com.example.skilift.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.example.skilift.R;
import com.example.skilift.fragments.ChatHistoryFragment;
import com.example.skilift.models.ChatItem;

public class ChatHistoryActivity extends AppCompatActivity implements ChatHistoryFragment.OnListFragmentInteractionListener {

    private ChatHistoryFragment chFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        Toolbar myToolbar = findViewById(R.id.historyToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        chFrag = ChatHistoryFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.chatHistoryLayout, chFrag, "CHAT_FRAG").commit();
    }

    @Override
    public void onListFragmentInteraction(ChatItem item) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
