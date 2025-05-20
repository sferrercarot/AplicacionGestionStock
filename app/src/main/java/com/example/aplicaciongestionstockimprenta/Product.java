package com.example.aplicaciongestionstockimprenta;

import com.google.gson.annotations.SerializedName;

public class Product {
    public int id;
    public String name;

    @SerializedName("cantidad_stock")
    public int cantidad_stock;

    @SerializedName("stock_bajo")
    public boolean stock_bajo;

    @SerializedName("image")
    private String image;

    private String categoria;


    public Product(int id, String name, int cantidad_stock, boolean stock_bajo, String image, String categoria) {
        this.id = id;
        this.name = name;
        this.cantidad_stock = cantidad_stock;
        this.stock_bajo = stock_bajo;
        this.image = image;
        this.categoria = categoria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCantidad_stock() {
        return cantidad_stock;
    }

    public void setCantidad_stock(int cantidad_stock) {
        this.cantidad_stock = cantidad_stock;
    }

    public boolean isStock_bajo() {
        return stock_bajo;
    }

    public void setStock_bajo(boolean stock_bajo) {
        this.stock_bajo = stock_bajo;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategoria() {
        return categoria;
    }
}
