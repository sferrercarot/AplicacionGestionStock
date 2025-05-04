package com.example.aplicaciongestionstockimprenta;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OdooService {

    @Headers("Content-Type: application/json")
    @POST("jsonrpc")
    Call<JsonObject> login(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("jsonrpc")
    Call<JsonObject> searchRead(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("jsonrpc")
    Call<JsonObject> callJsonRpc(@Body JsonObject body);
}
