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
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class StockListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProductsAdapter adapter;
    private OdooService service;
    private List<Product> productos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_list);

        Log.d("STOCK", "Entrando en StockListActivity");

        recyclerView = findViewById(R.id.rvProducts);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductsAdapter(productos);
        recyclerView.setAdapter(adapter);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(190, TimeUnit.SECONDS)
                .readTimeout(190, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://50.85.209.163:8069/")
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(OdooService.class);

        // ✅ Obtener datos reales del login
        int uid = getIntent().getIntExtra("uid", -1);
        String usuario = getIntent().getStringExtra("usuario");
        String password = getIntent().getStringExtra("password");

        if (uid == -1 || usuario == null || password == null) {
            Toast.makeText(this, "Datos de sesión no válidos", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        cargarProductos(uid, password);
    }

    private void cargarProductos(int uid, String password) {
        progressBar.setVisibility(View.VISIBLE);

        JsonObject body = OdooRequestBuilder.buildSearchReadRequest(
                "gestion_almacen", uid, password, "gestion_almacen.producto",
                new String[]{"id", "name", "cantidad_stock", "stock_bajo"}
        );

        service.searchRead(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressBar.setVisibility(View.GONE);

                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject json = response.body();
                        Log.d("STOCK", "Respuesta JSON: " + json.toString());

                        if (json.has("result") && json.get("result").isJsonArray()) {
                            JsonArray resultArray = json.getAsJsonArray("result");
                            productos.clear();
                            for (JsonElement elem : resultArray) {
                                JsonObject p = elem.getAsJsonObject();
                                int id = p.get("id").getAsInt();
                                String name = p.get("name").getAsString();
                                int stock = p.get("cantidad_stock").getAsInt();
                                boolean bajo = p.has("stock_bajo") && p.get("stock_bajo").getAsBoolean();
                                productos.add(new Product(id, name, stock, bajo));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(StockListActivity.this, "Sin resultados para mostrar", Toast.LENGTH_SHORT).show();
                        }
                    } else {
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
