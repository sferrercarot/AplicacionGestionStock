package com.example.aplicaciongestionstockimprenta;

import com.google.gson.annotations.SerializedName;

public class Product {
    public int id;
    public String name;

    @SerializedName("cantidad_stock")
    public int cantidad_stock;

    @SerializedName("stock_bajo")
    public boolean stock_bajo;

    public Product(int id, String name, int cantidad_stock, boolean stock_bajo) {
        this.id = id;
        this.name = name;
        this.cantidad_stock = cantidad_stock;
        this.stock_bajo = stock_bajo;
    }
}
