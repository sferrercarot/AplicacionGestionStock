package com.example.aplicaciongestionstockimprenta.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicaciongestionstockimprenta.network.OdooRequestBuilder;
import com.example.aplicaciongestionstockimprenta.network.OdooService;
import com.example.aplicaciongestionstockimprenta.models.Product;
import com.example.aplicaciongestionstockimprenta.adapters.ProductsAdapter;
import com.example.aplicaciongestionstockimprenta.R;
import com.example.aplicaciongestionstockimprenta.network.RetrofitClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProductsAdapter adapter;
    private OdooService service;
    private final List<Product> productos = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private String rol;
    private int uid;
    private String usuario;
    private String password;

    private boolean filtroAlmacenPapelActivo = false;
    private boolean filtroImpresionDigitalActivo = false;
    private boolean filtroImpresionOffsetActivo = false;
    private boolean filtroOtrosActivo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_list);

        uid = getIntent().getIntExtra("uid", -1);
        usuario = getIntent().getStringExtra("usuario");
        password = getIntent().getStringExtra("password");
        rol = getIntent().getStringExtra("rol");

        if (uid == -1 || usuario == null || password == null) {
            Toast.makeText(this, "Datos de sesión no válidos", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.rvProducts);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductsAdapter(this, productos, uid, password);
        recyclerView.setAdapter(adapter);

        service = RetrofitClient.getOdooService();
        cargarProductos();

        Button btnAbrirFiltros = findViewById(R.id.btnAbrirFiltros);
        btnAbrirFiltros.setOnClickListener(v -> {
            View popupView = getLayoutInflater().inflate(R.layout.bottom_sheet_filtros, null);

            PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );

            popupWindow.setElevation(8f);
            popupWindow.showAsDropDown(v);

            CheckBox filtroAlmacenPapel = popupView.findViewById(R.id.filtroAlmacenPapel);
            CheckBox filtroImpresionDigital = popupView.findViewById(R.id.filtroImpresionDigital);
            CheckBox filtroImpresionOffset = popupView.findViewById(R.id.filtroImpresionOffset);
            CheckBox filtroOtros = popupView.findViewById(R.id.filtroOtros);
            Button btnAplicar = popupView.findViewById(R.id.btnAplicarFiltros);

            filtroAlmacenPapel.setChecked(filtroAlmacenPapelActivo);
            filtroImpresionDigital.setChecked(filtroImpresionDigitalActivo);
            filtroImpresionOffset.setChecked(filtroImpresionOffsetActivo);
            filtroOtros.setChecked(filtroOtrosActivo);

            btnAplicar.setOnClickListener(aplicar -> {
                filtroAlmacenPapelActivo = filtroAlmacenPapel.isChecked();
                filtroImpresionDigitalActivo = filtroImpresionDigital.isChecked();
                filtroImpresionOffsetActivo = filtroImpresionOffset.isChecked();
                filtroOtrosActivo = filtroOtros.isChecked();

                List<String> categoriasSeleccionadas = new ArrayList<>();
                if (filtroAlmacenPapelActivo) categoriasSeleccionadas.add("Almacen General de Papel");
                if (filtroImpresionDigitalActivo) categoriasSeleccionadas.add("Impresión Digital");
                if (filtroImpresionOffsetActivo) categoriasSeleccionadas.add("Impresión Offset");
                if (filtroOtrosActivo) categoriasSeleccionadas.add("Otros");

                adapter.filtrarPorCategorias(categoriasSeleccionadas);
                popupWindow.dismiss();
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarProductos();
    }

    private void cargarProductos() {
        progressBar.setVisibility(View.VISIBLE);

        JsonObject body = OdooRequestBuilder.buildSearchReadRequest(
                "gestion_almacen", uid, password, "gestion_almacen.producto",
                new String[]{"id", "name", "cantidad_stock", "stock_bajo", "image", "categoria"}
        );

        service.searchRead(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> {
                        try {
                            JsonObject json = response.body();
                            List<Product> nuevosProductos = new ArrayList<>();

                            if (json.has("result") && json.get("result").isJsonArray()) {
                                JsonArray resultArray = json.getAsJsonArray("result");

                                for (JsonElement elem : resultArray) {
                                    if (elem.isJsonObject()) {
                                        JsonObject p = elem.getAsJsonObject();
                                        int id = p.has("id") ? p.get("id").getAsInt() : -1;
                                        String name = p.has("name") ? p.get("name").getAsString() : "Sin nombre";
                                        int stock = p.has("cantidad_stock") ? p.get("cantidad_stock").getAsInt() : 0;
                                        boolean bajo = p.has("stock_bajo") && p.get("stock_bajo").getAsBoolean();
                                        String image = p.get("image").getAsString();
                                        String categoria = p.has("categoria") ? p.get("categoria").getAsString() : "Otros";

                                        nuevosProductos.add(new Product(id, name, stock, bajo, image, categoria));
                                    }
                                }

                                runOnUiThread(() -> {
                                    adapter.actualizarDatos(nuevosProductos);
                                    progressBar.setVisibility(View.GONE);
                                });
                            } else {
                                runOnUiThread(() -> {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(StockListActivity.this, "Respuesta inesperada", Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(StockListActivity.this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(StockListActivity.this, "Error al obtener productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(StockListActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
