package com.example.aplicaciongestionstockimprenta;

public class Solicitud {
    private int id;
    private String fecha;
    private String usuario;
    private String mensaje;
    private String estado;

    private String producto;


    public Solicitud(int id, String fecha, String usuario, String mensaje, String estado, String producto) {
        this.id = id;
        this.fecha = fecha;
        this.usuario = usuario;
        this.mensaje = mensaje;
        this.estado = estado;
        this.producto = producto;
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

}
