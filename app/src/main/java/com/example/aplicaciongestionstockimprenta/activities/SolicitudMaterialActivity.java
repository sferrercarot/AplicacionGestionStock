package com.example.aplicaciongestionstockimprenta.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aplicaciongestionstockimprenta.network.OdooService;
import com.example.aplicaciongestionstockimprenta.models.Product;
import com.example.aplicaciongestionstockimprenta.R;
import com.example.aplicaciongestionstockimprenta.network.RetrofitClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SolicitudMaterialActivity extends AppCompatActivity {

    private Spinner spinnerProductos;
    private EditText etMensajeSolicitud;
    private Button btnEnviarSolicitud;

    private OdooService service;
    private List<Product> listaProductos = new ArrayList<>();
    private int productoSeleccionadoId = -1;
    private int uid;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_material);

        uid = getIntent().getIntExtra("uid", -1);
        password = getIntent().getStringExtra("password");

        spinnerProductos = findViewById(R.id.spinnerProductos);
        etMensajeSolicitud = findViewById(R.id.etMensajeSolicitud);
        btnEnviarSolicitud = findViewById(R.id.btnEnviarSolicitud);
        service = RetrofitClient.getOdooService();

        cargarProductos();

        btnEnviarSolicitud.setOnClickListener(v -> enviarSolicitud());
    }

    private void cargarProductos() {
        JsonObject body = new JsonObject();
        body.addProperty("jsonrpc", "2.0");
        body.addProperty("method", "call");
        body.add("params", new JsonObject());

        service.obtenerProductos(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("PRODUCTOS", "Productos obtenidos correctamente");

                    JsonObject outerResult = response.body().getAsJsonObject("result");
                    JsonArray results = outerResult.getAsJsonArray("result");
                    for (JsonElement elem : results) {
                        JsonObject obj = elem.getAsJsonObject();
                        int id = obj.get("id").getAsInt();
                        String name = obj.get("name").getAsString();
                        int cantidad = obj.get("cantidad_stock").getAsInt();
                        boolean bajo = obj.get("stock_bajo").getAsBoolean();
                        String image = obj.get("image").getAsString();
                        String categoria = obj.has("categoria") && !obj.get("categoria").isJsonNull() ? obj.get("categoria").getAsString() : "Otros";
                        listaProductos.add(new Product(id, name, cantidad, bajo, image, categoria));
                    }

                    ArrayAdapter<Product> adapter = new ArrayAdapter<>(SolicitudMaterialActivity.this,
                            android.R.layout.simple_spinner_item, listaProductos);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProductos.setAdapter(adapter);

                    spinnerProductos.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                            productoSeleccionadoId = listaProductos.get(position).getId();
                        }

                        @Override
                        public void onNothingSelected(android.widget.AdapterView<?> parent) {
                            productoSeleccionadoId = -1;
                        }
                    });

                } else {
                    try {
                        String errorMsg = response.errorBody() != null ? response.errorBody().string() : "sin errorBody";
                        Log.e("PRODUCTOS", "Error al cargar productos - código: " + response.code() + " - error: " + errorMsg);
                    } catch (Exception e) {
                        Log.e("PRODUCTOS", "Error parsing errorBody", e);
                    }
                    Toast.makeText(SolicitudMaterialActivity.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("PRODUCTOS", "Error de red al cargar productos", t);
                Toast.makeText(SolicitudMaterialActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarSolicitud() {
        String mensaje = etMensajeSolicitud.getText().toString().trim();

        if (mensaje.isEmpty()) {
            Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
            return;
        }

        if (productoSeleccionadoId == -1) {
            Toast.makeText(this, "Selecciona un producto", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject body = new JsonObject();
        JsonObject params = new JsonObject();
        params.addProperty("mensaje", mensaje);
        params.addProperty("producto_id", productoSeleccionadoId);
        body.add("params", params);

        service.solicitarMaterial(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SolicitudMaterialActivity.this, "Solicitud enviada", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(SolicitudMaterialActivity.this, "Error al enviar solicitud", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(SolicitudMaterialActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
