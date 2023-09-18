package com.example.selu;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Product> productList;
    private FirebaseFirestore db;
    private CartAdapterListener listener;

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }
    public CartAdapter(List<Product> productList) {
        this.productList = productList;
    }


    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.nameTextView.setText("Nama Produk: " + product.getNama());
        DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
        String formattedPrice = "Harga: Rp. " + decimalFormat.format(product.getHarga());

        holder.priceTextView.setText(formattedPrice);
        holder.quantityTextView.setText("Jumlah: " + String.valueOf(product.getJumlah()));




        int imageResource = 0;

        if ("Kemplang".equals(product.getNama())) {
            imageResource = R.drawable.kemplango; // Replace with the actual resource ID for the "kemplang" image
        } else if ("Bahan Mentah".equals(product.getNama())) {
            imageResource = R.drawable.bhnmentah0; // Replace with the actual resource ID for the "bhnmentah" image
        } else if ("Mentah".equals(product.getNama())) {
            imageResource = R.drawable.mentah0; // Replace with the actual resource ID for the "mentah" image
        } else if ("Sambal".equals(product.getNama())) {
            imageResource = R.drawable.sambal0; // Replace with the actual resource ID for the "cabe" image
        } else if ("Giling".equals(product.getNama())) {
            imageResource = R.drawable.giling0; // Replace with the actual resource ID for the "giling" image
        }

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onProductSelected(product);
            }
        });
        holder.productImageView.setImageResource(imageResource);
        holder.deleteButton.setOnClickListener(view -> {
            // Get the ID or unique identifier of the product
            String productId = product.getProductId();



            db = FirebaseFirestore.getInstance();
            CollectionReference productsCollection = db.collection("products");
            DocumentReference productRef = productsCollection.document(productId);
            productRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        // The product is successfully deleted
                        productList.remove(position);
                        notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e -> {
                        // Failed to delete the product
                        Log.e("CartAdapter", "Error deleting product", e);
                    });
        });
    }



    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        public ImageView productImageView; // Added ImageView for product image
        public TextView nameTextView;
        public TextView priceTextView;
        public TextView quantityTextView;
        public ImageButton deleteButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView); // Initialize ImageView
            nameTextView = itemView.findViewById(R.id.namaTextView);
            priceTextView = itemView.findViewById(R.id.hargaTextView);
            quantityTextView = itemView.findViewById(R.id.jumlahTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

    }
    public List<Product> getProductList() {
        return productList;
    }
}

