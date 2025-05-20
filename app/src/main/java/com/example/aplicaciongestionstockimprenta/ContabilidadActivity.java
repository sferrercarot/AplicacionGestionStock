package com.example.aplicaciongestionstockimprenta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
        setContentView(R.layout.activity_admin);  // Usa el mismo layout que AdminActivity

        Log.d(TAG, "onCreate: ContabilidadActivity arrancado");

        // Recuperar extras
        uid = getIntent().getIntExtra("uid", -1);
        usuario = getIntent().getStringExtra("usuario");
        password = getIntent().getStringExtra("password");
        rol = getIntent().getStringExtra("rol");
        sessionId = getIntent().getStringExtra("sessionId");

        if (uid == -1 || usuario == null || password == null) {
            Toast.makeText(this, "Datos de sesión no válidos", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Enlazar botones
        btnVerStock = findViewById(R.id.btnVerStock);
        btnSolicitarMaterial = findViewById(R.id.btnSolicitarMaterial);
        btnVerSolicitudes = findViewById(R.id.btnVerSolicitudes);

        // Ocultar botón de solicitar material (no permitido para contabilidad)
        btnSolicitarMaterial.setVisibility(View.GONE);

        // Ver Stock
        btnVerStock.setOnClickListener(v -> {
            Log.d(TAG, "btnVerStock.onClick: lanzando StockListActivity");

            Intent i = new Intent(ContabilidadActivity.this, StockListActivity.class);
            i.putExtra("uid", uid);
            i.putExtra("usuario", usuario);
            i.putExtra("password", password);
            i.putExtra("rol", rol);
            i.putExtra("sessionId", sessionId);
            startActivity(i);
        });

        // Ver solicitudes
        btnVerSolicitudes.setOnClickListener(v -> {
            Log.d(TAG, "btnVerSolicitudes.onClick: lanzando BuzonSolicitudesActivity");

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
