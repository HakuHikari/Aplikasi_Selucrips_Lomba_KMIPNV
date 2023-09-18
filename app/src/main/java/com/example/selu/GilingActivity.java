package com.example.selu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

public class GilingActivity extends AppCompatActivity {

    private DatabaseReference productsRef;
    private TextView angka;
    private RadioGroup kemasanRadioGroup;
    private Button addToCartButton;
    private String username;
    private int count;
    private double hargaPerGram;
    private double totalHarga;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giling);
        db = FirebaseFirestore.getInstance();
        angka = findViewById(R.id.angka);
        kemasanRadioGroup = findViewById(R.id.kemasanRadioGroup);
        addToCartButton = findViewById(R.id.addToCartButton);
        productsRef = FirebaseDatabase.getInstance().getReference("products");


        hargaPerGram = 1000;

        kemasanRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                calculateTotalPrice();
            }
        });

        angka.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Kosongkan
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotalPrice();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Kosongkan
            }
        });
    }

    public void incrementCount(View view) {
        count++;
        angka.setText(String.valueOf(count));
    }

    public void decrementCount(View view) {
        if (count > 0) {
            count--;
            angka.setText(String.valueOf(count));
        }
    }

    public void calculateTotalPrice() {
        int selectedRadioButtonId = kemasanRadioGroup.getCheckedRadioButtonId();

        double harga250gr = 25000;
        double harga500gr = 40000;
        double harga1kg = 70000;

        switch (selectedRadioButtonId) {
            case R.id.radio250gr:
                totalHarga = count * harga250gr;
                break;
            case R.id.radio500gr:
                totalHarga = count * harga500gr;
                break;
            case R.id.radio1kg:
                totalHarga = count * harga1kg;
                break;
        }

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(localeID);
        String formattedTotalHarga = formatter.format(totalHarga);

        TextView totalharga = findViewById(R.id.totalharga);
        totalharga.setText(formattedTotalHarga);
    }

    public void backToMainActivity(View view) {
        // Create an Intent to go back to the MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void addToCart(View view) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "Default Username");
        String productName = "Giling";
        double productPrice = totalHarga;
        int productQuantity = count;

        String productId = UUID.randomUUID().toString();
        Product product = new Product(productName, productPrice, productQuantity, username);

        product.setProductId(productId);


        db.collection("products")
                .document(productId)
                .set(product)
                .addOnSuccessListener(aVoid -> {

                    Intent cartIntent = new Intent(GilingActivity.this, MainActivity.class);
                    startActivity(cartIntent);
                    finish();})
                .addOnFailureListener(e -> {
                    // Handle the failure if adding the product fails.
                });
    }

}