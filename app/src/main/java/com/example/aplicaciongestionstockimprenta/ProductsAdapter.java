package com.example.aplicaciongestionstockimprenta;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.VH> {

    private final List<Product> data;
    private final Context context;
    private final int uid;
    private final String password;

    public ProductsAdapter(Context context, List<Product> data, int uid, String password) {
        this.context = context;
        this.data = data;
        this.uid = uid;
        this.password = password;
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

        if (p.stock_bajo) {
            holder.tvWarning.setVisibility(View.VISIBLE);
            holder.tvWarning.setText("¡Stock por debajo del mínimo!");
        } else {
            holder.tvWarning.setVisibility(View.GONE);
        }

        // OnClick → abrir detalle
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("id", p.id);
            intent.putExtra("name", p.name);
            intent.putExtra("cantidad_stock", p.cantidad_stock);
            intent.putExtra("uid", uid);
            intent.putExtra("password", password);
            context.startActivity(intent);
        });
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
            tvWarning = v.findViewById(R.id.tvWarning);
        }
    }
}
