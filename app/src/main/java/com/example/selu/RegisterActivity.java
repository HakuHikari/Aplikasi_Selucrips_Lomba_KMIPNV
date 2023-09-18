package com.example.selu;

import static com.example.selu.LoginActivity.fromHtml;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
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


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class RegisterActivity extends AppCompatActivity {

    private int currentId = 1;
    private EditText TxUsername, TxPassword, TxConPassword;
    private Button Btnregister;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TxUsername = (EditText) findViewById(R.id.txUsernameReg);
        TxPassword = (EditText) findViewById(R.id.txPasswordReg);
        TxConPassword = (EditText) findViewById(R.id.txConPassword);
        Btnregister = (Button) findViewById(R.id.btnRegister);

        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://selu-2f4d2-default-rtdb.firebaseio.com/");

        TextView tvRegister = (TextView) findViewById(R.id.tvRegister);

        tvRegister.setText(fromHtml("Kembali Ke Halaman" +
                "</font><font color='#3b5998'> Login</font>"));

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        Btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = TxUsername.getText().toString().trim();
                String password = TxPassword.getText().toString().trim();
                String conPassword = TxConPassword.getText().toString().trim();

                ContentValues values = new ContentValues();

                if (!password.equals(conPassword)) {
                    Toast.makeText(RegisterActivity.this, "Password Tidak Sama", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty() || username.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Username or Password Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if the username already exists in Firebase
                    DatabaseReference usernameRef = database.child("users");
                    Query query = usernameRef.orderByChild("username").equalTo(username);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Username already exists
                                Toast.makeText(RegisterActivity.this, "Username already taken", Toast.LENGTH_SHORT).show();
                            } else {
                                // Continue with user registration since username is available
                                DatabaseReference idRef = database.child("currentId");
                                idRef.runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        Integer currentId = mutableData.getValue(Integer.class);
                                        if (currentId == null) {
                                            mutableData.setValue(1);
                                        } else {
                                            mutableData.setValue(currentId + 1);
                                        }
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                                        if (committed) {
                                            Integer currentId = dataSnapshot.getValue(Integer.class);
                                            String userId = String.valueOf(currentId);


                                            User user = new User();
                                            user.setId(userId);
                                            user.setUsername(username);
                                            user.setPassword(password);
                                            user.setRole("user");

                                            // Mengirim data pengguna ke Firebase Realtime Database
                                            DatabaseReference userRef = database.child("users").child(userId);
                                            userRef.setValue(user);

                                            Toast.makeText(RegisterActivity.this, "Register successful", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(RegisterActivity.this, "Failed to check username availability", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    public static Spanned fromHtml(String html){
        Spanned result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        }else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}