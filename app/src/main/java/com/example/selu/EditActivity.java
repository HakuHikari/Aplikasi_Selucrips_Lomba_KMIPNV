package com.example.selu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditActivity extends AppCompatActivity {
    private EditText editTextEmail;
    private EditText editTextTelepon;
    private Button buttonSave;
    private DatabaseReference databaseReference;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextTelepon = findViewById(R.id.editTextTelepon);
        buttonSave = findViewById(R.id.buttonSave);

        // Retrieve username from Intent (passed from login activity)
        username = getIntent().getStringExtra("username");

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(username);

        // Load existing data from the database if available
        loadDataFromDatabase();

        // Save button click listener
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToDatabase();
            }
        });
    }

    private void loadDataFromDatabase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the node exists in the database
                if (dataSnapshot.exists()) {
                    // Retrieve existing data and populate the EditText fields
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String telepon = dataSnapshot.child("telepon").getValue(String.class);

                    if (email != null) {
                        editTextEmail.setText(email);
                    }

                    if (telepon != null) {
                        editTextTelepon.setText(telepon);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors here
            }
        });
    }

    private void saveDataToDatabase() {
        String email = editTextEmail.getText().toString().trim();
        String telepon = editTextTelepon.getText().toString().trim();

        // Update the data in the database
        databaseReference.child("email").setValue(email);
        databaseReference.child("telepon").setValue(telepon);

        // Show a toast message indicating successful save
        Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
    }
}
