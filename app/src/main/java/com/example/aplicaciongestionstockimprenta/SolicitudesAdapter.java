package com.example.aplicaciongestionstockimprenta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.estados_solicitud, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerEstado.setAdapter(adapter);

        // Setear el estado actual
        String estado = solicitud.getEstado();
        int spinnerPosition = adapter.getPosition(estado);
        holder.spinnerEstado.setSelection(spinnerPosition);

        // Cambiar el estado si se selecciona otro
        holder.spinnerEstado.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            boolean primeraVez = true;
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                String nuevoEstado = parent.getItemAtPosition(pos).toString();
                if (!primeraVez && !nuevoEstado.equals(solicitud.getEstado())) {
                    actualizarEstado(solicitud, nuevoEstado);
                }
                primeraVez = false;
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void actualizarEstado(Solicitud solicitud, String nuevoEstado) {
        JsonObject body = new JsonObject();
        JsonObject params = new JsonObject();
        params.addProperty("id", solicitud.getId());
        params.addProperty("estado", nuevoEstado);
        body.add("params", params);

        service.actualizarEstadoSolicitud(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    solicitud.setEstado(nuevoEstado);
                    Toast.makeText(context, "Estado actualizado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error al actualizar estado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context, "Fallo de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class SolicitudViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvUsuario, tvMensaje;
        Spinner spinnerEstado;

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
            tvMensaje = itemView.findViewById(R.id.tvMensaje);
            spinnerEstado = itemView.findViewById(R.id.spinnerEstado);
        }
    }
}
