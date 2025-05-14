package com.example.aplicaciongestionstockimprenta;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuzonSolicitudesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SolicitudesAdapter adapter;
    private List<Solicitud> listaSolicitudes = new ArrayList<>();
    private int uid;
    private String password;
    private OdooService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buzon_solicitudes);

        uid = getIntent().getIntExtra("uid", -1);
        password = getIntent().getStringExtra("password");
        service = RetrofitClient.getOdooService();

        recyclerView = findViewById(R.id.recyclerSolicitudes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SolicitudesAdapter(this, listaSolicitudes, uid, password);
        recyclerView.setAdapter(adapter);

        cargarSolicitudes();
    }

    private void cargarSolicitudes() {
        JsonObject body = new JsonObject();
        body.addProperty("type", "json");

        service.obtenerSolicitudes(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonArray results = response.body().getAsJsonArray("result");
                    listaSolicitudes.clear();
                    for (JsonElement elem : results) {
                        JsonObject obj = elem.getAsJsonObject();
                        int id = obj.get("id").getAsInt();
                        String mensaje = obj.get("mensaje").getAsString();
                        String fecha = obj.get("fecha").getAsString();
                        String estado = obj.get("estado").getAsString();
                        String usuario = obj.getAsJsonArray("usuario").get(1).getAsString();
                        listaSolicitudes.add(new Solicitud(id, mensaje, fecha, usuario, estado));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(BuzonSolicitudesActivity.this, "❌ Error al cargar solicitudes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(BuzonSolicitudesActivity.this, "❌ Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
