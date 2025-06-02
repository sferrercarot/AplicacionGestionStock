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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.VH> {

    private final Context context;
    private final int uid;
    private final String password;

    private final List<Product> listaOriginal;
    private List<Product> listaFiltrada;

    public ProductsAdapter(Context context, List<Product> data, int uid, String password) {
        this.context = context;
        this.uid = uid;
        this.password = password;
        this.listaOriginal = new ArrayList<>(data);
        this.listaFiltrada = new ArrayList<>(data);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Product p = listaFiltrada.get(position);

        holder.tvName.setText(p.name);
        holder.tvQty.setText("Stock: " + p.cantidad_stock);

        if (p.stock_bajo) {
            holder.tvWarning.setVisibility(View.VISIBLE);
            holder.tvWarning.setText("¡Stock por debajo del mínimo!");
        } else {
            holder.tvWarning.setVisibility(View.GONE);
        }

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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("id", p.id);
            intent.putExtra("name", p.name);
            intent.putExtra("cantidad_stock", p.cantidad_stock);
            intent.putExtra("uid", uid);
            intent.putExtra("password", password);
            intent.putExtra("image", p.getImage());
            context.startActivity(intent);
        });

        holder.btnPlus.setOnClickListener(v -> {
            int nuevaCantidad = p.cantidad_stock + 1;
            p.cantidad_stock = nuevaCantidad;
            holder.tvQty.setText("Stock: " + nuevaCantidad);
            actualizarStockEnOdoo(p.id, nuevaCantidad);
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (p.cantidad_stock > 0) {
                int nuevaCantidad = p.cantidad_stock - 1;
                p.cantidad_stock = nuevaCantidad;
                holder.tvQty.setText("Stock: " + nuevaCantidad);
                actualizarStockEnOdoo(p.id, nuevaCantidad);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

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
        notifyDataSetChanged();
    }

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
                } else {
                    JsonObject body = response.body();
                    if (body != null && body.has("error")) {
                    } else if (body != null && body.has("result")) {
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("StockUpdate", "Fallo de red al conectar con Odoo: " + t.getMessage());
            }
        });
    }


    public void actualizarDatos(List<Product> nuevosProductos) {
        listaOriginal.clear();
        listaOriginal.addAll(nuevosProductos);

        listaFiltrada.clear();
        listaFiltrada.addAll(nuevosProductos);

        notifyDataSetChanged();
    }
}
