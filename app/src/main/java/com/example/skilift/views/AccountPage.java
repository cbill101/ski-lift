package com.example.skilift.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.example.skilift.models.User;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

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

        signOutButton.setBackground(getDrawable(R.drawable.button_default));

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

        FirebaseUser user = mAuth.getCurrentUser();

        Uri profPic = user.getPhotoUrl();

        if(profPic != null) {
            Log.w(TAG, "test url" + profPic.toString());

            Glide.with(this)
                    .load(profPic)
                    .centerCrop()
                    .apply(RequestOptions.circleCropTransform())
                    .into(profPicView);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
