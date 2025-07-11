package com.example.aplicaciongestionstockimprenta.activities;

import android.os.Bundle;
import android.util.Log;
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

        // Obtiene datos de sesión
        uid = getIntent().getIntExtra("uid", -1);
        usuario = getIntent().getStringExtra("usuario");
        password = getIntent().getStringExtra("password");
        rol = getIntent().getStringExtra("rol");

        // Valida los datos recibidos
        if (uid == -1 || usuario == null || password == null) {
            Toast.makeText(this, "Datos de sesión no válidos", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Configura el RecyclerView y el adaptador
        recyclerView = findViewById(R.id.rvProducts);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductsAdapter(this, productos, uid, password);
        recyclerView.setAdapter(adapter);

        // Inicializa Retrofit
        service = RetrofitClient.getOdooService();

        // Carga los productos al arrancar
        cargarProductos();

        // Configura el botón de filtros (muestra un popup para aplicar filtros por categoría)
        Button btnAbrirFiltros = findViewById(R.id.btnAbrirFiltros);
        btnAbrirFiltros.setOnClickListener(v -> {
            // Infla el layout del popup desde XML
            View popupView = getLayoutInflater().inflate(R.layout.bottom_sheet_filtros, null);

            // Crea la ventana emergente
            PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );

            popupWindow.setElevation(8f);
            popupWindow.showAsDropDown(v); // Muestra debajo del botón

            // Referencias a los checkboxes y botón de aplicar
            CheckBox filtroAlmacenPapel = popupView.findViewById(R.id.filtroAlmacenPapel);
            CheckBox filtroImpresionDigital = popupView.findViewById(R.id.filtroImpresionDigital);
            CheckBox filtroImpresionOffset = popupView.findViewById(R.id.filtroImpresionOffset);
            CheckBox filtroOtros = popupView.findViewById(R.id.filtroOtros);
            Button btnAplicar = popupView.findViewById(R.id.btnAplicarFiltros);

            // Marca los filtros previamente activos
            filtroAlmacenPapel.setChecked(filtroAlmacenPapelActivo);
            filtroImpresionDigital.setChecked(filtroImpresionDigitalActivo);
            filtroImpresionOffset.setChecked(filtroImpresionOffsetActivo);
            filtroOtros.setChecked(filtroOtrosActivo);

            // Aplica los filtros seleccionados y actualiza el adaptador
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

    // Cada vez que se vuelve a esta pantalla, recarga los datos
    @Override
    protected void onResume() {
        super.onResume();
        cargarProductos();
    }

    // Método que obtiene la lista de productos desde el backend
    private void cargarProductos() {
        progressBar.setVisibility(View.VISIBLE); // Muestra barra de carga

        // Crea la petición JSON para obtener productos con los campos necesarios
        JsonObject body = OdooRequestBuilder.buildSearchReadRequest(
                "gestion_almacen", uid, password, "gestion_almacen.producto",
                new String[]{"id", "name", "cantidad_stock", "stock_bajo", "image", "categoria"}
        );

        // Hace la petición al backend
        service.searchRead(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> {
                        try {
                            JsonObject json = response.body();
                            Log.d("StockListActivity", "JSON recibido: " + json.toString());

                            List<Product> nuevosProductos = new ArrayList<>();

                            if (json.has("result") && json.get("result").isJsonArray()) {
                                JsonArray resultArray = json.getAsJsonArray("result");

                                for (JsonElement elem : resultArray) {
                                    if (elem.isJsonObject()) {
                                        JsonObject p = elem.getAsJsonObject();

                                        // Extrae y valida cada campo del producto
                                        int id = p.has("id") ? p.get("id").getAsInt() : -1;
                                        String name = p.has("name") ? p.get("name").getAsString() : "Sin nombre";
                                        String tipo = p.has("tipo") ? p.get("tipo").getAsString() : "Sin tipo";
                                        String medida = p.has("medida") ? p.get("medida").getAsString() : "0 x 0";
                                        int gramaje = p.has("gramaje") ? p.get("gramaje").getAsInt() : 0;
                                        int cantidad_actual = p.has("cantidad_actual") ? p.get("cantidad_actual").getAsInt() : 0;
                                        boolean cantidad_minima = p.has("cantidad_minima") && !p.get("cantidad_minima").isJsonNull() && p.get("cantidad_minima").getAsBoolean();
                                        String image = p.get("image").getAsString();
                                        String categoria = p.has("categoria") ? p.get("categoria").getAsString() : "Palet";

                                        nuevosProductos.add(new Product(id, name, tipo, gramaje, medida, categoria, cantidad_actual, cantidad_minima, image));
                                    }
                                }

                                // Actualiza la UI desde el hilo principal
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
                            Log.e("StockListActivity", "Error al procesar datos", e);  // Aquí imprime el error completo con stacktrace
                            Log.e("StockListActivity", "JSON problemático: " + (response.body() != null ? response.body().toString() : "null"));
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