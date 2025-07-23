package com.example.aplicaciongestionstockimprenta.models;

import com.google.gson.annotations.SerializedName;

public class Product {
    public int id;
    public String name;

    @SerializedName("tipo")
    private String tipo;

    @SerializedName("gramaje")
    private int gramaje;

    @SerializedName("medida")
    private String medida;

    private String categoria;

    @SerializedName("cantidad_actual")
    public int cantidad_actual;

    @SerializedName("cantidad_minima")
    public boolean cantidad_minima;

    @SerializedName("image")
    private String image;


    public Product(int id, String name, String tipo, int gramaje, String medida, String categoria, int cantidad_actual, boolean cantidad_minima, String image) {
        this.id = id;
        this.name = name;
        this.tipo = tipo;
        this.gramaje = gramaje;
        this.medida = medida;
        this.categoria = categoria;
        this.cantidad_actual = cantidad_actual;
        this.cantidad_minima = cantidad_minima;
        this.image = image;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getGramaje() {
        return gramaje;
    }

    public void setGramaje(int gramaje) {
        this.gramaje = gramaje;
    }

    public String getMedida() {
        return medida;
    }

    public void setMedida(String medida) {
        this.medida = medida;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getCantidad_actual() {
        return cantidad_actual;
    }

    public void setCantidad_actual(int cantidad_actual) {
        this.cantidad_actual = cantidad_actual;
    }

    public boolean isCantidad_minima() {
        return cantidad_minima;
    }

    public void setCantidad_minima(boolean cantidad_minima) {
        this.cantidad_minima = cantidad_minima;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return name + " " + tipo;
    }
}
