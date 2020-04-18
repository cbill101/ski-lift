package com.example.skilift.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.skilift.R;
import com.example.skilift.fragments.ProviderFragment;
import com.example.skilift.interfaces.OnResultClickListener;
import com.example.skilift.models.Provider;
import com.example.skilift.models.RideRequest;
import com.google.firebase.auth.FirebaseAuth;

public class RideListActivity extends AppCompatActivity implements OnResultClickListener {
    private Button confirmButton;
    private TextView descText;
    private ProviderFragment listFragment;
    private Provider selectedProvider;
    private RideRequest selectedRequest;
    private boolean isProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_list);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        confirmButton = findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(v -> {
            if(selectedRequest != null) {
                selectedRequest.setChecked(false);
                openWaitScreen();
            }
            else if (selectedProvider != null){
                selectedProvider.setChecked(false);
                openPayment();
            }
        });

        Intent intent = getIntent();
        isProvider = intent.getBooleanExtra("Provider", false);

        if (savedInstanceState != null) {
            listFragment = (ProviderFragment) getSupportFragmentManager().findFragmentByTag("RESULTS_FRAG");
            isProvider = savedInstanceState.getBoolean("isProvider");
            selectedProvider = savedInstanceState.getParcelable("selectedProv");
            selectedRequest = savedInstanceState.getParcelable("selectedReq");

            if (selectedRequest != null || selectedProvider != null) {
                confirmButton.setVisibility(View.VISIBLE);
            }
            else {
                confirmButton.setVisibility(Button.INVISIBLE);
            }
        }
        else {
            listFragment = new ProviderFragment(isProvider);
            listFragment.setOnResultClickListener(this);

            getSupportFragmentManager().beginTransaction().add(R.id.listFragLayout, listFragment, "RESULTS_FRAG").commit();

            confirmButton.setVisibility(Button.INVISIBLE);
        }

        descText = findViewById(R.id.textView);

        if(isProvider) {
            descText.setText(R.string.list_of_requests);
            confirmButton.setText(R.string.confirm);
        }
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    //For now goes to next page
    private void openWaitScreen() {
        Intent intent = new Intent(this, Wait.class);
        intent.putExtra("requestUID", selectedRequest.getUID());
        startActivity(intent);
    }

    //For now goes to next page
    private void openPayment() {
        Intent intent = new Intent(this, Payment.class);
        intent.putExtra("providerUID", selectedProvider.getUID());
        intent.putExtra("amountCharged",selectedProvider.getPrice().substring(1));
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isProvider", isProvider);

        if(selectedRequest != null) {
            outState.putParcelable("selectedReq", selectedRequest);
        }
        else if (selectedProvider != null) {
            outState.putParcelable("selectedProv", selectedProvider);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    public void onItemClick(Parcelable result, int position) {
        if(confirmButton.getVisibility() == View.INVISIBLE)
            confirmButton.setVisibility(View.VISIBLE);

        if(result instanceof Provider)
            selectedProvider = (Provider) result;
        else if (result instanceof RideRequest)
            selectedRequest = (RideRequest) result;
    }
}
