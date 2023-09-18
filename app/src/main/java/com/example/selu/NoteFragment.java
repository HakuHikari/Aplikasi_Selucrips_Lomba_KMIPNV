package com.example.selu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class NoteFragment extends Fragment {

    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NoteFragment() {
        // Required empty public constructor
    }


    public static NoteFragment newInstance(String param1, String param2) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        db = FirebaseFirestore.getInstance();

        sharedPreferences = requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        String loggedInUsername = getLoggedInUsername();



        fetchPesananData();

        return view;
    }

    private void fetchPesananData() {
        db.collection("pesanan")
                .whereEqualTo("username", getLoggedInUsername())
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
        TextView pesananTextView = requireView().findViewById(R.id.Pesanan);
        StringBuilder stringBuilder = new StringBuilder();

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        for (Pesanan pesanan : pesananList) {
            stringBuilder.append("Nama Lengkap: ").append(pesanan.getNamaLengkap()).append("\n");
            stringBuilder.append("Alamat: ").append(pesanan.getAlamat()).append("\n");
            stringBuilder.append("Kelurahan: ").append(pesanan.getKelurahan()).append("\n");
            stringBuilder.append("Kota: ").append(pesanan.getKota()).append("\n");
            stringBuilder.append("Nomor HP: ").append(pesanan.getKodePos()).append("\n");

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
    private String getLoggedInUsername() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        return sharedPreferences.getString("username", "Default Username");
    }
}






