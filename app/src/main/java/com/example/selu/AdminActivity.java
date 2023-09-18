package com.example.selu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);

        String username = sharedPreferences.getString("username", "");
        TextView tvHiUsername = findViewById(R.id.tvHiUsername);
        tvHiUsername.setText("Hi, "+username);



        fetchPesananData();


        ImageButton profilButton = findViewById(R.id.profil);
        profilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileActivity();
            }
        });
    }


    private void fetchPesananData () {
        db.collection("pesanan")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Pesanan> pesananList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Pesanan pesanan = documentSnapshot.toObject(Pesanan.class);
                        pesananList.add(pesanan);
                    }

                    displayPesananData(pesananList);
                })
                .addOnFailureListener(e -> {
                    // Handle the error if needed
                });
    }
    private void displayPesananData(List<Pesanan> pesananList) {
        TextView pesananTextView = findViewById(R.id.Pesanan);
        StringBuilder stringBuilder = new StringBuilder();

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        for (Pesanan pesanan : pesananList) {
            stringBuilder.append("Nama Lengkap: ").append(pesanan.getNamaLengkap()).append("\n");
            stringBuilder.append("Alamat: ").append(pesanan.getAlamat()).append("\n");
            stringBuilder.append("Kelurahan: ").append(pesanan.getKelurahan()).append("\n");
            stringBuilder.append("Kota: ").append(pesanan.getKota()).append("\n");
            stringBuilder.append("Nomor: ").append(pesanan.getKodePos()).append("\n\n");

            // Display product details for each pesanan
            List<Products> productsList = pesanan.getProducts(); // Assuming that Pesanan class has a method to get the product list
            for (Products product : productsList) {
                stringBuilder.append("Nama Produk: ").append(product.getNamaproduk()).append("\n");
                String formattedHarga = formatRupiah.format(product.getHarga());
                stringBuilder.append("Harga: ").append(formattedHarga).append("\n");
                stringBuilder.append("Jumlah: ").append(product.getJumlah()).append("\n\n");
            }

            double totalHarga = pesanan.getTotalHarga(); // Anggap method getTotalHarga() mengembalikan nilai double
            String formattedTotalHarga = formatRupiah.format(totalHarga);
            stringBuilder.append("Total Harga: ").append(formattedTotalHarga).append("\n\n");
            stringBuilder.append("-------------------------\n\n");

        }

        pesananTextView.setText(stringBuilder.toString());
    }




    public void openProfileActivity() {
        Intent intent = new Intent(this, ProfilActivity.class);
        // Pass any additional data using intent.putExtra() if needed
        startActivity(intent);
        finish();
    }
}
