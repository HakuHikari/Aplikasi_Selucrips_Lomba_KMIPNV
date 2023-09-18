package com.example.selu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfilActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    private ImageView profileImageView;
    private TextView nameTextView;

    private String username;

    private ImageView infoImageView;
    private ImageView keranjangImageView;

    private TextView emailTextView;
    private TextView teleponTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        nameTextView = findViewById(R.id.name);
        infoImageView = findViewById(R.id.icon_info);


        // Ambil username dari SharedPreferences


        ImageView logoutButton = findViewById(R.id.icon_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Menghapus status login dari SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("logged_in", false);
                editor.remove("role");
                editor.remove("username");
                editor.apply();

                // Keluar dari akun Firebase
                firebaseAuth.signOut();

                // Kembali ke halaman login
                Intent loginIntent = new Intent(ProfilActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish(); // Mengakhiri activity saat ini
            }
        });

        infoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cartIntent = new Intent(ProfilActivity.this, InfoActivity.class);
                startActivity(cartIntent);
            }
        });

        // Atur onClickListener untuk ImageView info


        retrieveDataFromFirebase();
    }

    private void retrieveDataFromFirebase() {
        username = sharedPreferences.getString("username", "Default Username");
        nameTextView.setText(username);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = usersRef.orderByChild("username").equalTo(username);


    }


    public void backToMainActivity(View view) {
        Intent intent = new Intent(ProfilActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Optionally, you can finish the current activity if you don't want to keep it in the stack.
    }
}


