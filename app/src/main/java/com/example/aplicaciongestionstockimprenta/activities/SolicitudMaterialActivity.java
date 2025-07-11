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

        // Obtiene los datos de sesión del usuario
        uid = getIntent().getIntExtra("uid", -1);
        password = getIntent().getStringExtra("password");

        // Vincula los elementos visuales del layout
        spinnerProductos = findViewById(R.id.spinnerProductos);
        etMensajeSolicitud = findViewById(R.id.etMensajeSolicitud);
        btnEnviarSolicitud = findViewById(R.id.btnEnviarSolicitud);

        // Inicializa Retrofit
        service = RetrofitClient.getOdooService();

        // Carga los productos desde el backend para mostrarlos en el Spinner
        cargarProductos();

        // Acción del botón: enviar la solicitud al backend
        btnEnviarSolicitud.setOnClickListener(v -> enviarSolicitud());
    }

    // Método que obtiene la lista de productos del backend (Odoo)
    private void cargarProductos() {
        // Cuerpo básico JSON-RPC para la petición
        JsonObject body = new JsonObject();
        body.addProperty("jsonrpc", "2.0");
        body.addProperty("method", "call");
        body.add("params", new JsonObject()); // sin filtros

        // Llama al endpoint que devuelve los productos
        service.obtenerProductos(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("PRODUCTOS", "Productos obtenidos correctamente");

                    // Accede al array de productos dentro del JSON de respuesta
                    JsonObject outerResult = response.body().getAsJsonObject("result");
                    JsonArray results = outerResult.getAsJsonArray("result");

                    // Recorre cada producto y lo añade a la lista local
                    for (JsonElement elem : results) {
                        JsonObject obj = elem.getAsJsonObject();
                        int id = obj.get("id").getAsInt();
                        String name = obj.get("name").getAsString();
                        String tipo = obj.get("tipo").getAsString();
                        int gramaje = obj.get("gramaje").getAsInt();
                        String medida = obj.get("medida").getAsString();
                        int cantidad_actual = obj.get("cantidad_actual").getAsInt();
                        boolean cantidad_minima = obj.get("cantidad_minima").getAsBoolean();
                        String image = obj.get("image").getAsString();
                        String categoria = obj.has("categoria") && !obj.get("categoria").isJsonNull()
                                ? obj.get("categoria").getAsString()
                                : "Otros";

                        listaProductos.add(new Product(id, name, tipo, gramaje, medida, categoria, cantidad_actual, cantidad_minima, image));
                    }

                    // Muestra los productos en el Spinner
                    ArrayAdapter<Product> adapter = new ArrayAdapter<>(
                            SolicitudMaterialActivity.this,
                            android.R.layout.simple_spinner_item,
                            listaProductos
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProductos.setAdapter(adapter);

                    // Guarda el ID del producto seleccionado
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
                    // En caso de error de servidor
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
                // Si falla la conexión
                Log.e("PRODUCTOS", "Error de red al cargar productos", t);
                Toast.makeText(SolicitudMaterialActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método que envía la solicitud de material al backend
    private void enviarSolicitud() {
        String mensaje = etMensajeSolicitud.getText().toString().trim();

        // Validaciones
        if (mensaje.isEmpty()) {
            Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
            return;
        }

        if (productoSeleccionadoId == -1) {
            Toast.makeText(this, "Selecciona un producto", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construye el cuerpo JSON para la solicitud
        JsonObject body = new JsonObject();
        JsonObject params = new JsonObject();
        params.addProperty("mensaje", mensaje);
        params.addProperty("producto_id", productoSeleccionadoId);
        body.add("params", params);

        // Llama al backend para guardar la solicitud
        service.solicitarMaterial(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SolicitudMaterialActivity.this, "Solicitud enviada", Toast.LENGTH_LONG).show();
                    finish(); // Cierra la actividad tras enviar
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