package com.example.aplicaciongestionstockimprenta.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicaciongestionstockimprenta.network.OdooService;
import com.example.aplicaciongestionstockimprenta.R;
import com.example.aplicaciongestionstockimprenta.network.RetrofitClient;
import com.example.aplicaciongestionstockimprenta.models.Solicitud;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SolicitudesAdapter extends RecyclerView.Adapter<SolicitudesAdapter.SolicitudViewHolder> {

    private final Context context;
    private final List<Solicitud> lista;
    private final int uid;
    private final String password;
    private final OdooService service;

    public SolicitudesAdapter(Context context, List<Solicitud> lista, int uid, String password) {
        this.context = context;
        this.lista = lista;
        this.uid = uid;
        this.password = password;
        this.service = RetrofitClient.getOdooService();
    }

    @NonNull
    @Override
    public SolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_solicitud, parent, false);
        return new SolicitudViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudViewHolder holder, int position) {
        Solicitud solicitud = lista.get(position);
        holder.tvFecha.setText(solicitud.getFecha());
        holder.tvUsuario.setText(solicitud.getUsuario());
        holder.tvMensaje.setText(solicitud.getMensaje());
        holder.tvProducto.setText(solicitud.getProducto());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.estados_solicitud, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerEstado.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition(solicitud.getEstado());
        holder.spinnerEstado.setSelection(spinnerPosition);

        String estado = solicitud.getEstado();
        if ("Pendiente".equalsIgnoreCase(estado)) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.estado_pendiente));
            holder.tvFecha.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            holder.tvUsuario.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            holder.tvMensaje.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            holder.tvProducto.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            holder.cardView.setCardElevation(8);
        } else if ("Resuelto".equalsIgnoreCase(estado)) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.estado_resuelto));
            int gris = ContextCompat.getColor(context, R.color.texto_resuelto);
            holder.tvFecha.setTextColor(gris);
            holder.tvUsuario.setTextColor(gris);
            holder.tvMensaje.setTextColor(gris);
            holder.tvProducto.setTextColor(gris);
            holder.cardView.setCardElevation(2);
        }

        holder.spinnerEstado.post(() -> {
            holder.spinnerEstado.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                boolean primeraVez = true;

                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                    String nuevoEstado = parent.getItemAtPosition(pos).toString().toLowerCase();
                    String estadoActual = solicitud.getEstado().toLowerCase();

                    Log.d("SPINNER", "Seleccionado: " + nuevoEstado + " | Actual: " + estadoActual);

                    if (!nuevoEstado.equals(estadoActual)) {
                        Log.d("SPINNER", "Cambio real detectado. Llamando a actualizarEstado...");
                        solicitud.setEstado(capitalize(nuevoEstado));
                        actualizarEstado(solicitud, nuevoEstado);
                    }
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {}
            });
        });

        holder.opciones.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.opciones);
            popup.inflate(R.menu.menu_opciones_solicitud);
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_borrar) {
                    borrarSolicitud(solicitud);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    private void actualizarEstado(Solicitud solicitud, String nuevoEstado) {
        JsonObject body = new JsonObject();
        body.addProperty("jsonrpc", "2.0");
        body.addProperty("type", "json");

        JsonObject params = new JsonObject();
        params.addProperty("id", solicitud.getId());
        params.addProperty("estado", nuevoEstado);
        body.add("params", params);

        Log.d("ADAPTER", "Enviando a Odoo: " + body.toString());

        service.actualizarEstadoSolicitud(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.d("ADAPTER", "Respuesta exitosa de Odoo: " + response.body());
                    Toast.makeText(context, "Estado actualizado", Toast.LENGTH_SHORT).show();
                    int index = lista.indexOf(solicitud);
                    if (index != -1) notifyItemChanged(index);
                } else {
                    Toast.makeText(context, "Error al actualizar estado", Toast.LENGTH_SHORT).show();
                    Log.e("ADAPTER", "Respuesta fallida: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context, "Fallo de red", Toast.LENGTH_SHORT).show();
                Log.e("ADAPTER", "Fallo al conectar con Odoo: ", t);
            }
        });
    }

    private void borrarSolicitud(Solicitud solicitud) {
        JsonObject body = new JsonObject();
        body.addProperty("jsonrpc", "2.0");
        body.addProperty("type", "json");

        JsonObject params = new JsonObject();
        params.addProperty("id", solicitud.getId());
        body.add("params", params);

        service.borrarSolicitud(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    lista.remove(solicitud);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Solicitud eliminada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error al eliminar solicitud", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context, "Fallo de red al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String capitalize(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class SolicitudViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvUsuario, tvMensaje, tvProducto;
        Spinner spinnerEstado;
        CardView cardView;
        ImageView opciones;

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.textFecha);
            tvUsuario = itemView.findViewById(R.id.textUsuario);
            tvMensaje = itemView.findViewById(R.id.textMensaje);
            tvProducto = itemView.findViewById(R.id.textProducto);
            spinnerEstado = itemView.findViewById(R.id.spinnerEstado);
            cardView = (CardView) itemView;
            opciones = itemView.findViewById(R.id.btnOpciones);
        }
    }
}
