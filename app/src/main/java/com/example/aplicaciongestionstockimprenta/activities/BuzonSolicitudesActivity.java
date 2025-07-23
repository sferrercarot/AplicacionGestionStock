package com.example.aplicaciongestionstockimprenta.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicaciongestionstockimprenta.network.OdooService;
import com.example.aplicaciongestionstockimprenta.R;
import com.example.aplicaciongestionstockimprenta.network.RetrofitClient;
import com.example.aplicaciongestionstockimprenta.models.Solicitud;
import com.example.aplicaciongestionstockimprenta.adapters.SolicitudesAdapter;
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

        // Recupera los datos de sesión enviados por la actividad anterior
        uid = getIntent().getIntExtra("uid", -1);
        password = getIntent().getStringExtra("password");

        // Obtiene una instancia del servicio Retrofit para conectarse a Odoo
        service = RetrofitClient.getOdooService();

        // Inicializa el RecyclerView y su layout manager
        recyclerView = findViewById(R.id.recyclerSolicitudes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializa el adaptador personalizado con la lista vacía y lo asocia al RecyclerView
        adapter = new SolicitudesAdapter(this, listaSolicitudes, uid, password);
        recyclerView.setAdapter(adapter);

        // Llama al método que carga las solicitudes desde Odoo
        cargarSolicitudes();
    }

    // Método para cargar las solicitudes de material desde el backend de Odoo
    private void cargarSolicitudes() {
        // Crea el cuerpo de la petición (necesario para algunas APIs en Odoo)
        JsonObject body = new JsonObject();
        body.addProperty("type", "json");

        // Llama al método definido en OdooService que obtiene las solicitudes
        service.obtenerSolicitudes(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // Si la respuesta es válida y contiene datos
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject outerResult = response.body().getAsJsonObject("result");

                    // Verifica si hay resultados en forma de array
                    if (outerResult != null && outerResult.has("result") && outerResult.get("result").isJsonArray()) {
                        JsonArray results = outerResult.getAsJsonArray("result");

                        // Limpia la lista actual y la rellena con las nuevas solicitudes
                        listaSolicitudes.clear();
                        for (JsonElement elem : results) {
                            JsonObject obj = elem.getAsJsonObject();

                            // Extrae los campos de cada solicitud del JSON
                            int id = obj.get("id").getAsInt();
                            String mensaje = obj.get("mensaje").getAsString();
                            String fecha = obj.get("fecha").getAsString();
                            String estado = obj.get("estado").getAsString();

                            // Formatea el estado para que empiece en mayúscula
                            estado = estado.substring(0, 1).toUpperCase() + estado.substring(1).toLowerCase();

                            String usuario = obj.get("usuario").getAsString();
                            JsonElement productoElement = obj.get("producto");

                            // Verifica si el campo producto está presente o es null
                            String producto = (productoElement != null && !productoElement.isJsonNull())
                                    ? productoElement.getAsString() : "Desconocido";

                            String tipo = (productoElement != null && !productoElement.isJsonNull())
                                    ? productoElement.getAsString() : "Desconocido";

                            // Añade la solicitud a la lista
                            listaSolicitudes.add(new Solicitud(id, mensaje, fecha, usuario, estado, producto, tipo));
                        }

                        // Notifica al adaptador que los datos han cambiado para que actualice la vista
                        adapter.notifyDataSetChanged();
                    } else {
                        // Si no hay resultados, muestra mensaje al usuario
                        Toast.makeText(BuzonSolicitudesActivity.this, "No hay solicitudes disponibles", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Si hay error en la respuesta del servidor
                    Toast.makeText(BuzonSolicitudesActivity.this, "Error al cargar solicitudes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Si ocurre un fallo en la conexión o servidor
                Toast.makeText(BuzonSolicitudesActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

}