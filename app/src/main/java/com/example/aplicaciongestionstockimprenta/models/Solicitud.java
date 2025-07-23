package com.example.aplicaciongestionstockimprenta.models;

public class Solicitud {
    private int id;
    private String fecha;
    private String usuario;
    private String mensaje;
    private String estado;

    private String producto;

    private String tipo;


    public Solicitud(int id, String fecha, String usuario, String mensaje, String estado, String producto, String tipo) {
        this.id = id;
        this.fecha = fecha;
        this.usuario = usuario;
        this.mensaje = mensaje;
        this.estado = estado;
        this.producto = producto;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public String getFecha() {
        return fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public String getProducto() {
        return producto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
