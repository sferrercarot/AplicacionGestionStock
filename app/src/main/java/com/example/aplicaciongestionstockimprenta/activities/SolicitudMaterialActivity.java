package com.example.aplicaciongestionstockimprenta.activities;

import static com.example.aplicaciongestionstockimprenta.network.OdooRequestBuilder.buildSearchReadRequest;

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
    private String password, db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_material);

        // Obtiene los datos de sesión del usuario
        db = getIntent().getStringExtra("db");
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

    private void cargarProductos() {
        // Define los campos que quieres obtener, incluyendo "tipo"
        String[] fields = {"name", "tipo", "gramaje", "medida", "cantidad_actual", "cantidad_minima", "image", "categoria"};

        // Construye el cuerpo JSON para la petición usando tu función buildSearchReadRequest
        JsonObject body = buildSearchReadRequest(db, uid, password, "gestion_almacen.producto", fields);

        service.obtenerProductos(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("PRODUCTOS", "Productos obtenidos correctamente");

                    JsonObject outerResult = response.body().getAsJsonObject("result");
                    JsonArray results = outerResult.getAsJsonArray("result");

                    listaProductos.clear();  // Limpia lista antes de añadir nuevos productos

                    for (JsonElement elem : results) {
                        JsonObject obj = elem.getAsJsonObject();

                        // Log para ver el contenido bruto del campo "tipo"
                        if (obj.has("tipo")) {
                            Log.d("TIPO_BRUTO", "tipo raw JSON: " + obj.get("tipo").toString());
                        } else {
                            Log.d("TIPO_BRUTO", "tipo raw JSON: NO TIENE CAMPO tipo");
                        }

                        int id = obj.get("id").getAsInt();
                        String name = obj.has("name") && !obj.get("name").isJsonNull()
                                ? obj.get("name").getAsString()
                                : "Desconocido";



                        if (obj.has("tipo")) {
                            Log.d("TIPO_BRUTO", "tipo raw JSON: " + obj.get("tipo").toString());
                        } else {
                            Log.d("TIPO_BRUTO", "tipo raw JSON: NO TIENE CAMPO tipo");
                        }

                        String tipo = obj.has("tipo") && !obj.get("tipo").isJsonNull()
                                ? obj.get("tipo").getAsString()
                                : "NoVa";

                        int gramaje = obj.has("gramaje") && !obj.get("gramaje").isJsonNull()
                                ? obj.get("gramaje").getAsInt()
                                : 0;

                        String medida = obj.has("medida") && !obj.get("medida").isJsonNull()
                                ? obj.get("medida").getAsString()
                                : "Desconocido";

                        int cantidad_actual = obj.has("cantidad_actual") && !obj.get("cantidad_actual").isJsonNull()
                                ? obj.get("cantidad_actual").getAsInt()
                                : 0;

                        boolean cantidad_minima = obj.has("cantidad_minima") && !obj.get("cantidad_minima").isJsonNull()
                                ? obj.get("cantidad_minima").getAsBoolean()
                                : false;

                        String image = obj.has("image") && !obj.get("image").isJsonNull()
                                ? obj.get("image").getAsString()
                                : "";

                        String categoria = obj.has("categoria") && !obj.get("categoria").isJsonNull()
                                ? obj.get("categoria").getAsString()
                                : "Otros";

                        listaProductos.add(new Product(id, name, tipo, gramaje, medida, categoria, cantidad_actual, cantidad_minima, image));
                    }

                    ArrayAdapter<Product> adapter = new ArrayAdapter<>(
                            SolicitudMaterialActivity.this,
                            android.R.layout.simple_spinner_item,
                            listaProductos
                    );
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