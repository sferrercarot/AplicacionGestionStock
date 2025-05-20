package com.example.aplicaciongestionstockimprenta;

import com.google.gson.annotations.SerializedName;

public class RolResponse {

    @SerializedName("result")
    public Result result;

    public static class Result {
        @SerializedName("rol")
        public String rol;

        @SerializedName("usuario")
        public String usuario;

        @SerializedName("id")
        public int id;
    }
}
