package com.example.selu;

import java.util.List;

public class Pesanan {
    private String namalengkap;
    private String alamat;
    private String kelurahan;
    private String kota;
    private String kodepos;
    private double totalharga;
    private String image_url;
    private List<Products> products;



    public String getNamaLengkap() {
        return namalengkap;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getKelurahan() {
        return kelurahan;
    }

    public String getKota() {
        return kota;
    }

    public String getKodePos() {
        return kodepos;
    }

    public double getTotalHarga() {
        return totalharga;
    }

    public String getFormattedTotalHarga() {
        double totalHargaDouble = getTotalHarga() / 100.0;
        return String.format("%.2f", totalHargaDouble).replace(".", ",");
    }
    public List<Products> getProducts() {
        return products;
    }

    // Add a setter method for the product list
    public void setProducts(List<Products> products) {
        this.products = products;
    }
}
