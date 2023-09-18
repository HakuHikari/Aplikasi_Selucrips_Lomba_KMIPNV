package com.example.selu;

public class Products {
    private String namaproduk;
    private int harga;
    private int jumlah;

    // Constructor (you can create more constructors as needed)
    public Products() {
    }

    // Add getters and setters for the fields
    public String getNamaproduk() {
        return namaproduk;
    }

    public void setNamaproduk(String namaproduk) {
        this.namaproduk = namaproduk;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }
}

