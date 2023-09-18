package com.example.selu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PembayaranActivity extends AppCompatActivity {



    private static final int REQUEST_IMAGE_PICK = 1;

    private ImageButton imgUploadFoto;
    private Uri selectedImageUri;
    private EditText txNamaLengkap;
    private EditText txAlamat;
    private EditText txKelurahan;
    private EditText txKota;
    private EditText txKodePos;
    private TextView totalhargaTextView;
    private Button AddToPesanButton;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;

    private FirebaseFirestore firestoreDB;
    private FirebaseStorage storage;
    private StorageReference storageRef;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);
        firestoreDB = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        imgUploadFoto = findViewById(R.id.imgUploadFoto);
        txNamaLengkap = findViewById(R.id.txnamalengkap);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the cartAdapter with an empty list of products
        cartAdapter = new CartAdapter(new ArrayList<>());
        recyclerView.setAdapter(cartAdapter);

        txAlamat = findViewById(R.id.txalamat);
        txKelurahan = findViewById(R.id.txkelurahan);
        txKota = findViewById(R.id.txkota);
        txKodePos = findViewById(R.id.txkodepos);
        totalhargaTextView = findViewById(R.id.totalhargaTextView);
        AddToPesanButton = findViewById(R.id.addToPesanButton);
        ImageButton backButton = findViewById(R.id.back);

        double totalHarga = getIntent().getDoubleExtra("total_harga", 0.0);

        DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
        String formattedTotalPrice = "Rp. " + decimalFormat.format(totalHarga);
        totalhargaTextView.setText(formattedTotalPrice);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        imgUploadFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClicked();
            }
        });

        // Move this outside of the onCreate method
        AddToPesanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFirestore();
            }
        });


        fetchProductsFromFirestore();
    }


private void onSelectImageClicked() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("PembayaranActivity", "onActivityResult: Result received.");

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            // Get the selected image URI
            selectedImageUri = data.getData();
            Log.d("PembayaranActivity", "Selected Image URI: " + selectedImageUri.toString());
            Toast.makeText(this, "Image selected!", Toast.LENGTH_SHORT).show();


        }
        }





    private void addToFirestore() {
        String namaLengkap = ((EditText) findViewById(R.id.txnamalengkap)).getText().toString();
        String alamat = ((EditText) findViewById(R.id.txalamat)).getText().toString();
        String kelurahan = ((EditText) findViewById(R.id.txkelurahan)).getText().toString();
        String kota = ((EditText) findViewById(R.id.txkota)).getText().toString();
        String kodePos = ((EditText) findViewById(R.id.txkodepos)).getText().toString();
        double totalHarga = getIntent().getDoubleExtra("total_harga", 0.0);
        Log.d("PembayaranActivity", "addToFirestore: AddToPesanButton clicked.");
        List<Product> productList = cartAdapter.getProductList();


        // Check if an image is selected before proceeding
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload the image to Firebase Storage
        uploadImage(selectedImageUri, namaLengkap, alamat, kelurahan, kota, kodePos, totalHarga, productList);
    }


    // Helper method to upload the selected image to Firebase Storage
    private void uploadImage(Uri imageUri, String namaLengkap, String alamat, String kelurahan, String kota, String kodePos, double totalHarga, List<Product> productList) {
        Log.d("PembayaranActivity", "uploadImage: Uploading image...");
        StorageReference imageRef = storageRef.child("images/" + imageUri.getLastPathSegment());

        // Upload the image to Firebase Storage
        UploadTask uploadTask = imageRef.putFile(imageUri);
        Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }

            // Continue with the task to get the download URL
            return imageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Image uploaded successfully, get the download URL
                Uri downloadUri = task.getResult();



                // Create a new document in the "pesanan" collection and include the image URL
                HashMap<String, Object> data = getData(namaLengkap, alamat, kelurahan, kota, kodePos, totalHarga, downloadUri.toString(), productList);
                firestoreDB.collection("pesanan").add(data)
                        .addOnSuccessListener(documentReference -> {
                            // Document added successfully
                            Toast.makeText(this, "Data dan Gambar Bukti Sudah Masuk", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // Error adding document
                            Toast.makeText(this, "Error adding document to Firestore.", Toast.LENGTH_SHORT).show();
                        });

            } else {
                // Error uploading image
                Toast.makeText(this, "Error uploading image to Firebase Storage.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProductsFromFirestore() {
        firestoreDB.collection("products")
                .whereEqualTo("username", getLoggedInUsername())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product> productList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Convert Firestore document to Product object
                            Product product = document.toObject(Product.class);
                            productList.add(product);
                        }
                        // Update the cartAdapter with the new product list
                        cartAdapter.setProductList(productList);
                        cartAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("PembayaranActivity", "Error getting documents: ", task.getException());
                    }
                });
    }

    // Helper method to create a HashMap containing the data to be uploaded to Firestore
    private HashMap<String, Object> getData(String namaLengkap, String alamat, String kelurahan, String kota, String kodePos, double totalHarga, String imageUrl, List<Product> productList) {
        String username = getLoggedInUsername();
        HashMap<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("namalengkap", namaLengkap);
        data.put("alamat", alamat);
        data.put("kelurahan", kelurahan);
        data.put("kota", kota);
        data.put("kodepos", kodePos);
        data.put("totalharga", totalHarga);
        data.put("image_url", imageUrl); // Add the image URL to the data

        List<HashMap<String, Object>> productDataList = new ArrayList<>();
        for (Product product : productList) {
            HashMap<String, Object> productData = new HashMap<>();
            productData.put("namaproduk", product.getNama());
            productData.put("jumlah", product.getJumlah());
            productData.put("harga", product.getHarga());
            productDataList.add(productData);
        }
        data.put("products", productDataList);


        // Add more fields if needed
        startActivity(new Intent(PembayaranActivity.this, SuksesActivity.class));


        return data;
    }

    private String getLoggedInUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        return sharedPreferences.getString("username", "Default Username");
    }


}