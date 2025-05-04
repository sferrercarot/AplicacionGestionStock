package com.example.aplicaciongestionstockimprenta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.VH> {

    private final List<Product> data;

    public ProductsAdapter(List<Product> data) {
        this.data = data;
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
        Product p = data.get(position);

        holder.tvName.setText(p.name);
        holder.tvQty.setText("Stock: " + p.cantidad_stock);

        // Puedes añadir esto si quieres resaltar cuando hay poco stock
        if (p.stock_bajo) {
            holder.tvWarning.setVisibility(View.VISIBLE);
            holder.tvWarning.setText("¡Stock por debajo del mínimo!");
        } else {
            holder.tvWarning.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvWarning;

        VH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvQty = v.findViewById(R.id.tvQty);
            tvWarning = v.findViewById(R.id.tvWarning);  // Asegúrate de tener esto en item_product.xml
        }
    }
}
