package com.example.aplicaciongestionstockimprenta.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aplicaciongestionstockimprenta.R;
import com.example.aplicaciongestionstockimprenta.network.OdooService;
import com.example.aplicaciongestionstockimprenta.network.RetrofitClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NuevoPapelActivity extends AppCompatActivity {

    private EditText etMarca, etTipo, etGramaje, etMedida, etCantidadActual, etCantidadMinima;
    private Spinner spinnerCategoria;
    private Button btnGuardar;

    private OdooService odooService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_papel);

        int uid = getIntent().getIntExtra("uid", -1);
        String password = getIntent().getStringExtra("password");

        // Referenciar views
        etMarca = findViewById(R.id.et_marca);
        etTipo = findViewById(R.id.et_tipo);
        etGramaje = findViewById(R.id.et_gramaje);
        etMedida = findViewById(R.id.et_medida);

        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categoriaSeleccionada = parent.getItemAtPosition(position).toString();

                if (categoriaSeleccionada.equalsIgnoreCase("Sobres")) {
                    etGramaje.setVisibility(View.GONE);
                    etMedida.setVisibility(View.GONE);
                    etCantidadMinima.setVisibility(View.GONE);
                } else {
                    etGramaje.setVisibility(View.VISIBLE);
                    etMedida.setVisibility(View.VISIBLE);
                    etCantidadMinima.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nada
            }
        });

        etCantidadActual = findViewById(R.id.et_cantidad_actual);
        etCantidadMinima = findViewById(R.id.et_cantidad_minima);
        btnGuardar = findViewById(R.id.btn_guardar);

        // Inicializar servicio Odoo (adaptar a tu forma)
        odooService = RetrofitClient.getOdooService();

        cargarCategoriasDesdeOdoo("gestion_almacen", uid, password);


        btnGuardar.setOnClickListener(v -> guardarNuevoPapel());
    }

    private void guardarNuevoPapel() {
        String marca = etMarca.getText().toString().trim();
        String tipo = etTipo.getText().toString().trim();
        String gramaje = etGramaje.getText().toString().trim();
        String medida = etMedida.getText().toString().trim();
        String categoria = spinnerCategoria.getSelectedItem().toString().trim();
        String cantidadActualStr = etCantidadActual.getText().toString().trim();
        String cantidadMinimaStr = etCantidadMinima.getText().toString().trim();

        if (marca.isEmpty() || tipo.isEmpty() || gramaje.isEmpty() || medida.isEmpty() ||
                categoria.isEmpty() || cantidadActualStr.isEmpty() || cantidadMinimaStr.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidadActual, cantidadMinima;
        try {
            cantidadActual = Integer.parseInt(cantidadActualStr);
            cantidadMinima = Integer.parseInt(cantidadMinimaStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cantidad actual y mínima deben ser números", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener los datos necesarios para la llamada
        String db = "gestion_almacen"; // o pásalo en el Intent y obtén aquí
        int uid = getIntent().getIntExtra("uid", -1);
        String password = getIntent().getStringExtra("password");

        // Construcción del JSON para la llamada execute_kw create
        JsonObject body = new JsonObject();
        body.addProperty("jsonrpc", "2.0");
        body.addProperty("method", "call");

        JsonObject params = new JsonObject();
        params.addProperty("service", "object");
        params.addProperty("method", "execute_kw");

        JsonArray args = new JsonArray();
        args.add(db);
        args.add(uid);
        args.add(password);
        args.add("gestion_almacen.producto"); // modelo Odoo
        args.add("create");

        // Lista con un solo objeto: los campos a crear
        JsonObject campos = new JsonObject();
        campos.addProperty("name", marca);
        campos.addProperty("tipo", tipo);
        campos.addProperty("gramaje", gramaje);
        campos.addProperty("medida", medida);
        campos.addProperty("categoria", categoria);
        campos.addProperty("cantidad_actual", cantidadActual);
        campos.addProperty("cantidad_minima", cantidadMinima);

        JsonArray listaCampos = new JsonArray();
        listaCampos.add(campos);

        args.add(listaCampos);

        params.add("args", args);
        body.add("params", params);
        body.addProperty("id", 1);

        // Llamada Retrofit para crear el registro
        odooService.genericWrite(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject resBody = response.body();
                    Log.d("NuevoPapelActivity", "Respuesta completa: " + resBody.toString());
                    if (resBody.has("result")) {
                        Log.d("NuevoPapelActivity", "Respuesta contiene 'result'");
                        Toast.makeText(NuevoPapelActivity.this, "Papel creado con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.w("NuevoPapelActivity", "Respuesta sin resultado (no tiene 'result')");
                        Toast.makeText(NuevoPapelActivity.this, "Error: respuesta sin resultado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("NuevoPapelActivity", "Error en la respuesta. Código: " + response.code() + ", mensaje: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            String errorBodyStr = response.errorBody().string();
                            Log.e("NuevoPapelActivity", "ErrorBody: " + errorBodyStr);
                        } catch (IOException e) {
                            Log.e("NuevoPapelActivity", "Error leyendo errorBody", e);
                        }
                    }
                    Toast.makeText(NuevoPapelActivity.this, "Error al crear papel", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(NuevoPapelActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarCategoriasDesdeOdoo(String db, int uid, String password) {
        JsonArray fields = new JsonArray();
        fields.add("categoria");  // asegurarte de que este es el campo correcto en tu modelo

        JsonObject kw = new JsonObject();
        kw.add("fields", fields);

        JsonObject body = new JsonObject();
        body.addProperty("jsonrpc", "2.0");
        body.addProperty("method", "call");

        JsonObject params = new JsonObject();
        params.addProperty("service", "object");
        params.addProperty("method", "execute_kw");

        JsonArray args = new JsonArray();
        args.add(db);
        args.add(uid);
        args.add(password);
        args.add("gestion_almacen.producto"); // modelo de productos
        args.add("search_read");
        args.add(new JsonArray());  // dominio vacío
        args.add(kw);               // kwargs

        params.add("args", args);
        body.add("params", params);

        odooService.searchRead(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject responseBody = response.body();
                    JsonElement resultElement = responseBody.get("result");

                    if (resultElement != null && resultElement.isJsonArray()) {
                        JsonArray resultArray = resultElement.getAsJsonArray();

                        Set<String> categoriasUnicas = new HashSet<>();

                        for (JsonElement elem : resultArray) {
                            JsonObject obj = elem.getAsJsonObject();
                            if (obj.has("categoria") && !obj.get("categoria").isJsonNull()) {
                                String categoria = obj.get("categoria").getAsString();
                                categoriasUnicas.add(categoria);
                            }
                        }

                        List<String> nombresCategorias = new ArrayList<>(categoriasUnicas);
                        Collections.sort(nombresCategorias); // opcional, para mostrar ordenadas

                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    NuevoPapelActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    nombresCategorias
                            );
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCategoria.setAdapter(adapter);
                        });
                    } else {
                        Toast.makeText(NuevoPapelActivity.this, "No se encontraron categorías", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NuevoPapelActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(NuevoPapelActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e("NuevoPapelActivity", "Error: ", t);
            }
        });
    }
}

