package com.example.aplicaciongestionstockimprenta.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aplicaciongestionstockimprenta.R;

public class ContabilidadActivity extends AppCompatActivity {

    private static final String TAG = "ContabilidadActivity";

    private Button btnVerStock;
    private Button btnSolicitarMaterial;
    private Button btnVerSolicitudes;

    private int uid;
    private String usuario;
    private String password;
    private String rol;
    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Se reutiliza el mismo layout XML que AdminActivity (mismo diseño base)
        setContentView(R.layout.activity_admin);

        // Mensaje en el log para confirmar que la actividad se ha iniciado correctamente
        Log.d(TAG, "ContabilidadActivity arrancado");

        // Recupera los datos de sesión enviados por la actividad anterior
        uid = getIntent().getIntExtra("uid", -1);
        usuario = getIntent().getStringExtra("usuario");
        password = getIntent().getStringExtra("password");
        rol = getIntent().getStringExtra("rol");
        sessionId = getIntent().getStringExtra("sessionId");

        // Si faltan datos esenciales de sesión, se muestra un error y se cierra la actividad
        if (uid == -1 || usuario == null || password == null) {
            Toast.makeText(this, "Datos de sesión no válidos", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Vincula los botones definidos en el layout con las variables del código
        btnVerStock = findViewById(R.id.btnVerStock);
        btnSolicitarMaterial = findViewById(R.id.btnSolicitarMaterial);
        btnVerSolicitudes = findViewById(R.id.btnVerSolicitudes);

        // Esconde el botón "Solicitar material", ya que Contabilidad solo revisa, no solicita
        btnSolicitarMaterial.setVisibility(View.GONE);

        // Acción del botón "Ver Stock": abre la actividad que muestra la lista de productos
        btnVerStock.setOnClickListener(v -> {
            Log.d(TAG, "Lanzando StockListActivity");

            Intent i = new Intent(ContabilidadActivity.this, StockListActivity.class);
            i.putExtra("uid", uid);
            i.putExtra("usuario", usuario);
            i.putExtra("password", password);
            i.putExtra("rol", rol);
            i.putExtra("sessionId", sessionId);
            startActivity(i);
        });

        // Acción del botón "Ver Solicitudes": abre el buzón de solicitudes de material
        btnVerSolicitudes.setOnClickListener(v -> {
            Log.d(TAG, "Lanzando BuzonSolicitudesActivity");

            Intent i = new Intent(ContabilidadActivity.this, BuzonSolicitudesActivity.class);
            i.putExtra("uid", uid);
            i.putExtra("usuario", usuario);
            i.putExtra("password", password);
            i.putExtra("rol", rol);
            i.putExtra("sessionId", sessionId);
            startActivity(i);
        });
    }
}