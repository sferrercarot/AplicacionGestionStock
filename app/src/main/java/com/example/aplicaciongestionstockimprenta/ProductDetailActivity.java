package com.example.aplicaciongestionstockimprenta;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvProductName, tvCurrentStock;
    private EditText etNewStock;
    private Button btnUpdateStock;

    private int productId, currentStock, uid;
    private String productName, password;

    private OdooService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        tvProductName = findViewById(R.id.tvProductName);
        tvCurrentStock = findViewById(R.id.tvCurrentStock);
        etNewStock = findViewById(R.id.etNewStock);
        btnUpdateStock = findViewById(R.id.btnUpdateStock);

        // Recoger extras del intent
        productId = getIntent().getIntExtra("id", -1);
        productName = getIntent().getStringExtra("name");
        currentStock = getIntent().getIntExtra("cantidad_stock", 0);
        uid = getIntent().getIntExtra("uid", -1);
        password = getIntent().getStringExtra("password");

        if (productId == -1 || productName == null || uid == -1 || password == null) {
            Toast.makeText(this, "Datos incompletos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvProductName.setText(productName);
        tvCurrentStock.setText("Stock actual: " + currentStock);

        // Inicializar Retrofit
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1900, TimeUnit.SECONDS)
                .readTimeout(1900, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://50.85.209.163:8069/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(OdooService.class);

        btnUpdateStock.setOnClickListener(v -> {
            String newStockStr = etNewStock.getText().toString().trim();
            if (newStockStr.isEmpty()) {
                Toast.makeText(this, "Introduce un valor de stock", Toast.LENGTH_SHORT).show();
                return;
            }

            int newStock;
            try {
                newStock = Integer.parseInt(newStockStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Número no válido", Toast.LENGTH_SHORT).show();
                return;
            }

            actualizarStockEnOdoo(newStock);
        });
    }

    private void actualizarStockEnOdoo(int nuevoStock) {
        JsonObject body = OdooRequestBuilder.buildWriteRequest(
                "gestion_almacen", uid, password,
                "gestion_almacen.producto",
                productId,
                "cantidad_stock",
                nuevoStock
        );

        Log.d("PRODUCT_DETAIL", "Actualizando stock: " + body.toString());

        service.genericWrite(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("PRODUCT_DETAIL", "Stock actualizado correctamente");
                    Toast.makeText(ProductDetailActivity.this, "Stock actualizado", Toast.LENGTH_SHORT).show();
                    finish(); // Opcional: cerrar y volver atrás
                } else {
                    Log.e("PRODUCT_DETAIL", "Fallo al actualizar stock: " + response.code());
                    Toast.makeText(ProductDetailActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("PRODUCT_DETAIL", "Error de red", t);
                Toast.makeText(ProductDetailActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
