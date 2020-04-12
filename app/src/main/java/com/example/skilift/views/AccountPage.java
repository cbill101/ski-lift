package com.example.skilift.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.skilift.R;
import com.example.skilift.interfaces.FirebaseResultListener;
import com.example.skilift.models.User;
import com.example.skilift.viewmodels.InformationVM;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountPage extends AppCompatActivity {

    private static final String TAG = "AccountPage";
    private FirebaseAuth mAuth;
    private TextView emailAcc;
    private TextView userDisplayName;
    private TextView phoneDisplay;
    private ImageView profPicView;
    private GoogleSignInAccount googleAccount;
    private InformationVM infoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        infoViewModel = ViewModelProviders.of(this).get(InformationVM.class);

        googleAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();

        Toolbar myToolbar = findViewById(R.id.accountToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Account");

        emailAcc = findViewById(R.id.accEmail);
        userDisplayName = findViewById(R.id.accountPage_nameDisplay);
        phoneDisplay = findViewById(R.id.accountPage_phoneDisplay);
        profPicView = findViewById(R.id.profPicView);

        showUserInfo();

        Button signOutButton = findViewById(R.id.signOutButton);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(getApplicationContext(), "Sign out successful.",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(v.getContext(), LoginMenu.class));
                finish();
            }
        });
    }

    private void showUserInfo() {
        infoViewModel.getCurrentUser(new FirebaseResultListener<User>() {
            @Override
            public void onComplete(User result) {
                emailAcc.setText(result.getEmail());
                userDisplayName.setText(result.getName());
                phoneDisplay.setText(result.getPhone());
            }
        });

        infoViewModel.displayProfilePic(this, profPicView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
