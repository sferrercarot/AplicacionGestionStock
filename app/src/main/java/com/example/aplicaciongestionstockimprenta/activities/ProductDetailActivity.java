package com.example.aplicaciongestionstockimprenta.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aplicaciongestionstockimprenta.network.OdooRequestBuilder;
import com.example.aplicaciongestionstockimprenta.network.OdooService;
import com.example.aplicaciongestionstockimprenta.R;
import com.example.aplicaciongestionstockimprenta.network.RetrofitClient;
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
    private ImageView imgProduct;

    private int productId, currentStock, uid;
    private String productName, password, imageUrl;

    private OdooService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        tvProductName = findViewById(R.id.tvProductName);
        tvCurrentStock = findViewById(R.id.tvCurrentStock);
        etNewStock = findViewById(R.id.etNewStock);
        btnUpdateStock = findViewById(R.id.btnUpdateStock);
        imgProduct = findViewById(R.id.imgProductoDetalle);

        productId = getIntent().getIntExtra("id", -1);
        productName = getIntent().getStringExtra("name");
        currentStock = getIntent().getIntExtra("cantidad_stock", 0);
        uid = getIntent().getIntExtra("uid", -1);
        password = getIntent().getStringExtra("password");
        imageUrl = getIntent().getStringExtra("image");

        if (productId == -1 || productName == null || uid == -1 || password == null) {
            Toast.makeText(this, "Datos incompletos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvProductName.setText(productName);
        tvCurrentStock.setText("Stock actual: " + currentStock);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                String base64Data = imageUrl.contains(",") ? imageUrl.split(",")[1] : imageUrl;
                byte[] imageBytes = Base64.decode(base64Data, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                imgProduct.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                imgProduct.setImageResource(R.drawable.stock_box);
            }
        } else {
            imgProduct.setImageResource(R.drawable.stock_box);
        }

        service = RetrofitClient.getOdooService();


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
                    finish();
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
