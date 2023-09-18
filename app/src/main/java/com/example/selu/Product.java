package com.example.selu;




public class Product {

    private String  productId;
    private String nama;
    private double harga;
    private int jumlah;
    private String username;
    private String userID;

    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public Product() {

    }

    public Product(String nama, double harga, int jumlah, String username) {
        this.nama = nama;
        this.harga = harga;
        this.jumlah = jumlah;
        this.username = username;

    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
    public String getNama() {
        return nama;
    }

    public double getHarga() {
        return harga;
    }

    public int getJumlah() {
        return jumlah;
    }
    public String getUsername() {return username;}
}
