package com.example.aplicaciongestionstockimprenta.network;

import com.domatix.yevbes.nucleus.core.entities.session.authenticate.AuthenticateReqBody;
import com.domatix.yevbes.nucleus.core.entities.session.authenticate.Authenticate;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import com.example.aplicaciongestionstockimprenta.models.RolResponse;


public interface OdooService {
    @Headers("Content-Type: application/json")
    @POST("web/session/authenticate")
    Call<Authenticate> login(@Body AuthenticateReqBody body);

    @Headers("Content-Type: application/json")
    @POST("jsonrpc")
    Call<JsonObject> searchRead(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("api/rol_usuario")
    Call<RolResponse> obtenerRol(@Body JsonObject emptyBody);

    @POST("/jsonrpc")
    Call<JsonObject> genericWrite(@Body JsonObject body);

    @Headers("Content-Type: application/json")
    @POST("/api/productos")
    Call<JsonObject> obtenerProductos(@Body JsonObject body);

    @POST("/api/solicitar_material")
    Call<JsonObject> solicitarMaterial(@Body JsonObject body);

    @POST("/api/actualizar_estado_solicitud")
    Call<JsonObject> actualizarEstadoSolicitud(@Body JsonObject body);

    @POST("/api/obtener_solicitudes")
    Call<JsonObject> obtenerSolicitudes(@Body JsonObject body);

    @POST("/api/borrar_solicitud")
    Call<JsonObject> borrarSolicitud(@Body JsonObject body);
}
