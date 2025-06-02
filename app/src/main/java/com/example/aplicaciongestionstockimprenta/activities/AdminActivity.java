package com.example.aplicaciongestionstockimprenta.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aplicaciongestionstockimprenta.R;

public class AdminActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_admin);

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

        btnVerStock = findViewById(R.id.btnVerStock);
        btnSolicitarMaterial = findViewById(R.id.btnSolicitarMaterial);
        btnVerSolicitudes = findViewById(R.id.btnVerSolicitudes);

        btnVerStock.setOnClickListener(v -> {
            Intent i = new Intent(AdminActivity.this, StockListActivity.class);
            i.putExtra("uid", uid);
            i.putExtra("usuario", usuario);
            i.putExtra("password", password);
            i.putExtra("rol", rol);
            i.putExtra("sessionId", sessionId);
            startActivity(i);
        });

        btnSolicitarMaterial.setOnClickListener(v -> {
            Intent i = new Intent(AdminActivity.this, SolicitudMaterialActivity.class);
            i.putExtra("uid", uid);
            i.putExtra("password", password);
            startActivity(i);
        });


        btnVerSolicitudes.setOnClickListener(v -> {
            Intent i = new Intent(AdminActivity.this, BuzonSolicitudesActivity.class);
            i.putExtra("uid", uid);
            i.putExtra("usuario", usuario);
            i.putExtra("password", password);
            i.putExtra("rol", rol);
            i.putExtra("sessionId", sessionId);
            startActivity(i);
        });

    }
}
