package com.example.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RideListActivity extends AppCompatActivity {
    private Button confirmButton;
    private ListView listView;
    private FirebaseAuth mAuth;
    private ArrayList<Provider> rideList;
    private ArrayAdapter adapter;
    private String selectedProviderID;

    private RadioButton listRadioButton = null;
    int listIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_list);

        confirmButton = findViewById(R.id.confirmButton);
        listView = findViewById(R.id.listView);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listRadioButton != null) {
                    openPayment();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Must select a ride.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();

        // Array list to save from db
        rideList = new ArrayList<>();
        adapter = new ArrayAdapter<Provider>(this, R.layout.list_item, rideList);
        listView.setAdapter(adapter);

        CollectionReference dRef = FirebaseFirestore.getInstance().collection("Providers");

        dRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    rideList.clear();
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Provider info = new Provider(document.getData());
                        info.setUID(document.getId());
                        rideList.add(info);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void onClickRadioButton(View v) {
        View vMain = ((View) v.getParent());

        // uncheck previous checked button.
        if (listRadioButton != null) listRadioButton.setChecked(false);
        // assign to the variable the new one
        listRadioButton = (RadioButton) v;
        // find if the new one is checked or not, and set "listIndex"
        if (listRadioButton.isChecked()) {
            listIndex = ((ViewGroup) vMain.getParent()).indexOfChild(vMain);
        } else {
            listRadioButton = null;
            listIndex = -1;
        }
    }

    //For now goes to next page
    private void openPayment() {
        Intent intent = new Intent(this, Payment.class);

        startActivity(intent);
    }
}
