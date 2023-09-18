package com.example.selu;

import static android.text.Html.fromHtml;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private EditText TxUsername, TxPassword;
    private Button BtnLogin;
    private DatabaseReference database;

    private void saveUsernameToSharedPreferences(String username) {
        editor.putString("username", username);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        TxUsername = findViewById(R.id.txUsername);
        TxPassword = findViewById(R.id.txPassword);
        BtnLogin = findViewById(R.id.btnLogin);

        TextView tvCreateAccount = findViewById(R.id.tvCreateAccount);

        tvCreateAccount.setText(fromHtml("Belum Punya Akun?. " +
                "</font><font color='#3b5998'>Buat Akun</font>"));

        if (sharedPreferences.getBoolean("logged_in", false)) {
            String role = sharedPreferences.getString("role", "");
            if (role.equals("admin")) {
                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
            } else if (role.equals("user")) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
            finish();
        }

        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = TxUsername.getText().toString().trim();
                String password = TxPassword.getText().toString().trim();

                database = FirebaseDatabase.getInstance().getReference("users");

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Username atau password salah", Toast.LENGTH_SHORT).show();
                } else {
                    database.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                                User user = userSnapshot.getValue(User.class);

                                                if (user.getPassword().equals(password)) {
                                                    saveUsernameToSharedPreferences(username);


                                    if (user.getPassword().equals(password)) {
                                        Toast.makeText(getApplicationContext(), "Login Berhasil", Toast.LENGTH_SHORT).show();
                                        if (user.getRole().equals("admin")) {
                                            startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                                        } else if (user.getRole().equals("user")) {
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        }
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean("logged_in", true);
                                        editor.putString("role", user.getRole());
                                        editor.putString("username", user.getUsername());
                                        editor.apply();
                                        finish();
                                        return;
                                    }
                                    }
                                }
                                Toast.makeText(getApplicationContext(), "Password salah", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Username tidak ditemukan", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public static Spanned fromHtml(String html) {
        Spanned result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}

