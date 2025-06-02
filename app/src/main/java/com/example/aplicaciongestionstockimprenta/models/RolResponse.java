package com.example.aplicaciongestionstockimprenta.models;

import com.google.gson.annotations.SerializedName;

public class RolResponse {

    @SerializedName("result")
    public Result result;

    public static class Result {
        @SerializedName("rol")
        public String rol;

        @SerializedName("id")
        public int id;
    }
}
