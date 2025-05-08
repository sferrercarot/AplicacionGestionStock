package com.example.aplicaciongestionstockimprenta;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StockListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProductsAdapter adapter;
    private OdooService service;
    private List<Product> productos = new ArrayList<>();

    private String rol;
    private int uid;
    private String usuario;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_list);

        Log.d("STOCK", "Entrando en StockListActivity");

        // Recuperar datos del intent
        uid = getIntent().getIntExtra("uid", -1);
        usuario = getIntent().getStringExtra("usuario");
        password = getIntent().getStringExtra("password");
        rol = getIntent().getStringExtra("rol");

        Log.d("STOCK", "Rol recibido: " + rol);
        Log.d("STOCK", "UID: " + uid + ", Usuario: " + usuario);

        if (uid == -1 || usuario == null || password == null) {
            Toast.makeText(this, "Datos de sesión no válidos", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Inicializar UI
        recyclerView = findViewById(R.id.rvProducts);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductsAdapter(productos);
        recyclerView.setAdapter(adapter);

        // Crear cliente Retrofit sin cabeceras especiales
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(190, TimeUnit.SECONDS)
                .readTimeout(190, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://50.85.209.163:8069/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(OdooService.class);

        // Llamada para cargar productos
        cargarProductos();
    }

    private void cargarProductos() {
        progressBar.setVisibility(View.VISIBLE);

        JsonObject body = OdooRequestBuilder.buildSearchReadRequest(
                "gestion_almacen", uid, password, "gestion_almacen.producto",
                new String[]{"id", "name", "cantidad_stock", "stock_bajo"}
        );

        Log.d("STOCK", "JSON enviado a Odoo: " + body.toString());

        service.searchRead(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressBar.setVisibility(View.GONE);

                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject json = response.body();
                        Log.d("STOCK", "Respuesta JSON completa: " + json.toString());

                        if (json.has("result") && json.get("result").isJsonArray()) {
                            JsonArray resultArray = json.getAsJsonArray("result");
                            productos.clear();
                            for (JsonElement elem : resultArray) {
                                if (elem.isJsonObject()) {
                                    JsonObject p = elem.getAsJsonObject();
                                    int id = p.has("id") ? p.get("id").getAsInt() : -1;
                                    String name = p.has("name") ? p.get("name").getAsString() : "Sin nombre";
                                    int stock = p.has("cantidad_stock") ? p.get("cantidad_stock").getAsInt() : 0;
                                    boolean bajo = p.has("stock_bajo") && p.get("stock_bajo").getAsBoolean();
                                    productos.add(new Product(id, name, stock, bajo));
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.w("STOCK", "Respuesta sin campo 'result' válido");
                            Toast.makeText(StockListActivity.this, "Respuesta inesperada", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("STOCK", "Respuesta fallida: " + response.code());
                        Toast.makeText(StockListActivity.this, "Error al obtener productos", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("STOCK", "Error al procesar respuesta", e);
                    Toast.makeText(StockListActivity.this, "Error inesperado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("STOCK", "Error de red", t);
                Toast.makeText(StockListActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
