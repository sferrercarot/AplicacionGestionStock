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

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imgProductoDetalle;
    private TextView txtTituloProducto, tvCurrentStock, tvCategoria;
    private EditText etEntradaStock, etSalidaStock;
    private Button btnEntradaStock, btnSalidaStock;

    private int productId, currentStock, uid;
    private String productName, password, imageUrl;
    private String tipo, medida, categoria;
    private int gramaje;
    private boolean cantidadMinima;

    private OdooService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Inicializar vistas
        imgProductoDetalle = findViewById(R.id.imgProductoDetalle);
        txtTituloProducto = findViewById(R.id.txtTituloProducto);
        tvCurrentStock = findViewById(R.id.tvCurrentStock);
        tvCategoria = findViewById(R.id.tvCategoria);
        etEntradaStock = findViewById(R.id.etEntradaStock);
        etSalidaStock = findViewById(R.id.etSalidaStock);
        btnEntradaStock = findViewById(R.id.btnEntradaStock);
        btnSalidaStock = findViewById(R.id.btnSalidaStock);

        // Obtener datos del Intent
        productId = getIntent().getIntExtra("id", -1);
        productName = getIntent().getStringExtra("name");
        currentStock = getIntent().getIntExtra("cantidad_actual", 0);
        NumberFormat nf = NumberFormat.getInstance(new Locale("es", "ES"));
        String stockFormateado = nf.format(currentStock);

        uid = getIntent().getIntExtra("uid", -1);
        password = getIntent().getStringExtra("password");
        imageUrl = getIntent().getStringExtra("image");

        // Datos que no vas a mostrar directamente pero puedes guardar para lógica
        tipo = getIntent().getStringExtra("tipo");
        gramaje = getIntent().getIntExtra("gramaje", 0);
        medida = getIntent().getStringExtra("medida");
        categoria = getIntent().getStringExtra("categoria");
        cantidadMinima = getIntent().getBooleanExtra("cantidad_minima", false);

        // Poner datos en vistas visibles
        String tituloCompleto = productName;
        if (tipo != null && !tipo.isEmpty()) {
            tituloCompleto += " " + tipo;
        }
        if (gramaje > 0) {
            tituloCompleto += " - " + gramaje + "gr";
        }

        Log.d("DEBUG_MEDIDA", "Valor de medida: '" + medida + "'");
        if (medida != null && !medida.isEmpty() && !medida.equals("0")) {
            tituloCompleto += " - " + medida + "cm";
        }
        txtTituloProducto.setText(tituloCompleto);

        tvCurrentStock.setText("Stock actual: " + stockFormateado);
        tvCategoria.setText(categoria != null ? categoria : "");  // Por si viene null


        // Verifica que todos los datos necesarios estén presentes
        if (productId == -1 || productName == null || uid == -1 || password == null) {
            Toast.makeText(this, "Datos incompletos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Si hay una imagen en base64, se decodifica y se muestra
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                // Elimina el encabezado si existe ("data:image/png;base64,...")
                String base64Data = imageUrl.contains(",") ? imageUrl.split(",")[1] : imageUrl;
                byte[] imageBytes = Base64.decode(base64Data, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                imgProductoDetalle.setImageBitmap(bitmap);
            } catch (Exception e) {
                // Si falla, se muestra una imagen por defecto
                e.printStackTrace();
                imgProductoDetalle.setImageResource(R.drawable.stock_box);
            }
        } else {
            imgProductoDetalle.setImageResource(R.drawable.stock_box);
        }

        // Inicializa Retrofit
        service = RetrofitClient.getOdooService();

        // Acción al pulsar el botón "Actualizar stock"
        btnEntradaStock.setOnClickListener(v -> {
            String entradaStr = etEntradaStock.getText().toString();
            int entrada = entradaStr.isEmpty() ? 0 : Integer.parseInt(entradaStr);
            if (entrada <= 0) {
                Toast.makeText(this, "Introduce un valor positivo para entrada", Toast.LENGTH_SHORT).show();
                return;
            }
            int nuevoStock = currentStock + entrada;
            actualizarStockEnOdoo(nuevoStock);
        });

        btnSalidaStock.setOnClickListener(v -> {
            String salidaStr = etSalidaStock.getText().toString();
            int salida = salidaStr.isEmpty() ? 0 : Integer.parseInt(salidaStr);
            if (salida <= 0) {
                Toast.makeText(this, "Introduce un valor positivo para salida", Toast.LENGTH_SHORT).show();
                return;
            }
            int nuevoStock = currentStock - salida;
            if (nuevoStock < 0) nuevoStock = 0;
            actualizarStockEnOdoo(nuevoStock);
        });
    }

    // Método que envía la actualización del stock al backend (Odoo)
    private void actualizarStockEnOdoo(int nuevoStock) {
        JsonObject body = OdooRequestBuilder.buildWriteRequest(
                "gestion_almacen", uid, password,
                "gestion_almacen.producto",
                productId,
                "cantidad_actual",
                nuevoStock
        );

        Log.d("PRODUCT_DETAIL", "Actualizando stock: " + body.toString());

        service.genericWrite(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("PRODUCT_DETAIL", "Stock actualizado correctamente");
                    Toast.makeText(ProductDetailActivity.this, "Stock actualizado", Toast.LENGTH_SHORT).show();
                    finish(); // Cierra la actividad al finalizar
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