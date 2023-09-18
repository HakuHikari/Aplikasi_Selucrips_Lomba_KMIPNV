package com.example.selu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {

    private RecyclerView recyclerView;

    private TextView totalhargaTextView;
    private double totalHarga = 0.0;
    private CartAdapter cartAdapter;
    private List<Product> productList;

    FirebaseFirestore db;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView = view.findViewById(R.id.recyclerView);
        totalhargaTextView = view.findViewById(R.id.totalharga);
        Button paymentButton = view.findViewById(R.id.addtopayment);
        productList = new ArrayList<>();
        cartAdapter = new CartAdapter(productList);
        recyclerView.setAdapter(cartAdapter);


        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch data from Firestore
        fetchProductsFromFirestore();

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PembayaranActivity.class);

                intent.putExtra("total_harga", totalHarga);

                startActivity(intent);
            }
        });

        return view;
    }



    private void fetchProductsFromFirestore() {
        db.collection("products")
                .whereEqualTo("username", getLoggedInUsername())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            productList.clear();
                            totalHarga = 0.0; // Reset the total price

                            for (DocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                productList.add(product);

                                // Calculate the total price by summing up the prices of all products
                                totalHarga += product.getHarga();
                            }
                            cartAdapter.notifyDataSetChanged();

                            // Update the totalhargaTextView with the calculated total price
                            DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
                            String formattedTotalPrice = "Rp. " + decimalFormat.format(totalHarga);
                            totalhargaTextView.setText(formattedTotalPrice);
                        } else {
                            // Handle errors
                        }
                    }
                });
    }

    private String getLoggedInUsername() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        return sharedPreferences.getString("username", "Default Username");
    }
}

