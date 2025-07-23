package com.example.aplicaciongestionstockimprenta.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aplicaciongestionstockimprenta.R;
import com.example.aplicaciongestionstockimprenta.activities.ProductDetailActivity;
import com.example.aplicaciongestionstockimprenta.models.Product;
import com.example.aplicaciongestionstockimprenta.network.OdooRequestBuilder;
import com.example.aplicaciongestionstockimprenta.network.OdooService;
import com.example.aplicaciongestionstockimprenta.network.RetrofitClient;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Adaptador para mostrar productos en un RecyclerView
public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.VH> {

    private final Context context;
    private final int uid;
    private final String password;

    // Lista original (sin filtros) y lista filtrada (lo que se muestra)
    private final List<Product> listaOriginal;
    private List<Product> listaFiltrada;

    // Constructor: guarda el contexto, datos de usuario y copia las listas
    public ProductsAdapter(Context context, List<Product> data, int uid, String password) {
        this.context = context;
        this.uid = uid;
        this.password = password;
        this.listaOriginal = new ArrayList<>(data);
        this.listaFiltrada = new ArrayList<>(data);
    }

    // Crea el layout para cada fila del RecyclerView
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new VH(v);
    }

    // Asigna los valores a los elementos visuales de cada fila
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Product p = listaFiltrada.get(position);

        // Formateador de número para cantidad_actual
        NumberFormat nf = NumberFormat.getInstance(new Locale("es", "ES"));
        String stockFormateado = nf.format(p.cantidad_actual);
        holder.tvQty.setText("Stock: " + stockFormateado);

        // Mostrar campos dependiendo de la categoría
        if (p.getCategoria().equalsIgnoreCase("Sobres")) {
            // Solo mostrar nombre y tipo en la línea principal
            String fullName = p.name + " - " + p.getTipo();
            holder.tvName.setText(fullName.trim());

            // Ocultar campos que no aplican
            holder.tvWarning.setVisibility(View.GONE); // no hay cantidad mínima en sobres
        } else {
            // Mostrar todos los campos
            String fullName = p.name + " " + p.getTipo() + " - " + p.getGramaje() + "gr - " + p.getMedida() + "cm";
            holder.tvName.setText(fullName.trim());

            // Mostrar advertencia si está por debajo del stock mínimo
            if (p.cantidad_minima) {
                holder.tvWarning.setVisibility(View.VISIBLE);
                holder.tvWarning.setText("¡Stock por debajo del mínimo!");
            } else {
                holder.tvWarning.setVisibility(View.GONE);
            }
        }

        // Imagen del producto
        String base64Image = p.getImage();
        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                String base64Data = base64Image.contains(",") ? base64Image.split(",")[1] : base64Image;
                byte[] imageBytes = Base64.decode(base64Data, Base64.DEFAULT);

                Glide.with(context)
                        .asBitmap()
                        .load(imageBytes)
                        .placeholder(R.drawable.stock_box)
                        .into(holder.productImage);

            } catch (Exception e) {
                e.printStackTrace();
                holder.productImage.setImageResource(R.drawable.stock_box);
            }
        } else {
            holder.productImage.setImageResource(R.drawable.stock_box);
        }

        // Al pulsar en el producto se abre la pantalla de detalle
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("id", p.id);
            intent.putExtra("name", p.name);
            intent.putExtra("tipo", p.getTipo());
            intent.putExtra("gramaje", p.getGramaje());
            intent.putExtra("medida", p.getMedida());
            intent.putExtra("categoria", p.getCategoria());
            intent.putExtra("cantidad_actual", p.cantidad_actual);
            intent.putExtra("cantidad_minima", p.isCantidad_minima());
            intent.putExtra("image", p.getImage());
            intent.putExtra("uid", uid);
            intent.putExtra("password", password);
            context.startActivity(intent);
        });

        holder.btnPlus.setVisibility(View.GONE);
        holder.btnMinus.setVisibility(View.GONE);

/*
        // Botón "+" para incrementar el stock
        holder.btnPlus.setOnClickListener(v -> {
            int nuevaCantidad = p.cantidad_actual + 1;
            p.cantidad_actual = nuevaCantidad;
            holder.tvQty.setText("Stock: " + nuevaCantidad);
            actualizarStockEnOdoo(p.id, nuevaCantidad); // Sincroniza con Odoo
        });

        // Botón "-" para disminuir el stock (si es mayor que 0)
        holder.btnMinus.setOnClickListener(v -> {
            if (p.cantidad_actual > 0) {
                int nuevaCantidad = p.cantidad_actual - 1;
                p.cantidad_actual = nuevaCantidad;
                holder.tvQty.setText("Stock: " + nuevaCantidad);
                actualizarStockEnOdoo(p.id, nuevaCantidad); // Sincroniza con Odoo
            }
        });
*/

    }

    // Devuelve el número de productos visibles (filtrados)
    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    // Filtra la lista de productos por categorías seleccionadas
    public void filtrarPorCategorias(List<String> categorias) {
        if (categorias == null || categorias.isEmpty()) {
            listaFiltrada = new ArrayList<>(listaOriginal);
        } else {
            List<Product> resultado = new ArrayList<>();
            for (Product p : listaOriginal) {
                if (categorias.contains(p.getCategoria())) {
                    resultado.add(p);
                }
            }
            listaFiltrada = resultado;
        }
        notifyDataSetChanged(); // Refresca la lista
    }

    // ViewHolder: contiene las vistas de cada fila de producto
    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvWarning;
        ImageView productImage;
        Button btnPlus, btnMinus;

        VH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvQty = v.findViewById(R.id.tvQty);
            tvWarning = v.findViewById(R.id.tvWarning);
            productImage = v.findViewById(R.id.imgProduct);
            btnPlus = v.findViewById(R.id.btnPlus);
            btnMinus = v.findViewById(R.id.btnMinus);
        }
    }

    // Actualiza el stock en el backend (Odoo)
    private void actualizarStockEnOdoo(int productId, int nuevaCantidad) {
        JsonObject request = OdooRequestBuilder.buildWriteRequest(
                "gestion_almacen", uid, password,
                "gestion_almacen.producto",
                productId,
                "cantidad_stock",
                nuevaCantidad);

        Log.d("StockUpdate", "JSON enviado a Odoo: " + request.toString());

        OdooService service = RetrofitClient.getOdooService();
        Call<JsonObject> call = service.genericWrite(request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    // Si falla la actualización, podría añadirse lógica extra
                } else {
                    JsonObject body = response.body();
                    if (body != null && body.has("error")) {
                        // Se podría mostrar mensaje si hay error
                    } else if (body != null && body.has("result")) {
                        // Se ha actualizado correctamente
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("StockUpdate", "Fallo de red al conectar con Odoo: " + t.getMessage());
            }
        });
    }

    // Reemplaza los datos del adaptador con una nueva lista de productos
    public void actualizarDatos(List<Product> nuevosProductos) {
        listaOriginal.clear();
        listaOriginal.addAll(nuevosProductos);

        listaFiltrada.clear();
        listaFiltrada.addAll(nuevosProductos);

        notifyDataSetChanged(); // Refresca la vista
    }
}