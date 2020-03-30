package com.example.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountPage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView emailAcc;
    private TextView userDisplayName;
    private TextView phoneDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();

        Toolbar myToolbar = findViewById(R.id.accountToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Account");

        emailAcc = findViewById(R.id.accEmail);
        userDisplayName = findViewById(R.id.accountPage_nameDisplay);
        phoneDisplay = findViewById(R.id.accountPage_phoneDisplay);

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference dRef = db.collection("users").document(mAuth.getCurrentUser().getUid());

        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User user = new User(document.getData());
                        emailAcc.setText(user.getEmail());
                        userDisplayName.setText(user.getName());
                        phoneDisplay.setText(user.getPhone());
                    }
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
