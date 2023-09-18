package com.example.selu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class ProfilAdminActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    private ImageView profileImageView;
    private TextView nameTextView;

    private String username;

    private  ImageView infoImageView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_admin);

        sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        nameTextView = findViewById(R.id.name);

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
                Intent loginIntent = new Intent(ProfilAdminActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish(); // Mengakhiri activity saat ini
            }
        });
        infoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cartIntent = new Intent(ProfilAdminActivity.this, InfoActivity.class);
                startActivity(cartIntent);
            }
        });

    }
}