package com.example.aplicaciongestionstockimprenta;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

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

        VH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvQty = v.findViewById(R.id.tvQty);
            tvWarning = v.findViewById(R.id.tvWarning);
            productImage = v.findViewById(R.id.imgProduct);
        }
    }


    public void actualizarDatos(List<Product> nuevosProductos) {
        listaOriginal.clear();
        listaOriginal.addAll(nuevosProductos);

        listaFiltrada.clear();
        listaFiltrada.addAll(nuevosProductos);

        notifyDataSetChanged();
    }

}
