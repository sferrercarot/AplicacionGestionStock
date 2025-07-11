package com.example.aplicaciongestionstockimprenta.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aplicaciongestionstockimprenta.R;

public class AdminActivity extends AppCompatActivity {

    // Declaración de los botones que permiten navegar entre funcionalidades
    private Button btnVerStock;
    private Button btnSolicitarMaterial;
    private Button btnVerSolicitudes;

    // Variables para guardar los datos de sesión del usuario
    private int uid;
    private String usuario;
    private String password;
    private String rol;
    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Recupera los datos de sesión enviados desde la actividad anterior
        uid = getIntent().getIntExtra("uid", -1);
        usuario = getIntent().getStringExtra("usuario");
        password = getIntent().getStringExtra("password");
        rol = getIntent().getStringExtra("rol");
        sessionId = getIntent().getStringExtra("sessionId");

        // Verificación de que los datos básicos de sesión se han recibido correctamente
        if (uid == -1 || usuario == null || password == null) {
            // Muestra un mensaje de error si faltan datos y cierra la actividad
            Toast.makeText(this, "Datos de sesión no válidos", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Asocia los botones definidos en el XML con las variables del código
        btnVerStock = findViewById(R.id.btnVerStock);
        btnSolicitarMaterial = findViewById(R.id.btnSolicitarMaterial);
        btnVerSolicitudes = findViewById(R.id.btnVerSolicitudes);

        // Configura el botón "Ver Stock" para abrir la actividad de lista de stock
        btnVerStock.setOnClickListener(v -> {
            Intent i = new Intent(AdminActivity.this, StockListActivity.class);
            // Pasa los datos de sesión a la siguiente actividad
            i.putExtra("uid", uid);
            i.putExtra("usuario", usuario);
            i.putExtra("password", password);
            i.putExtra("rol", rol);
            i.putExtra("sessionId", sessionId);
            startActivity(i); // Inicia la nueva actividad
        });

        // Configura el botón "Solicitar Material" para abrir la actividad de solicitud
        btnSolicitarMaterial.setOnClickListener(v -> {
            Intent i = new Intent(AdminActivity.this, SolicitudMaterialActivity.class);
            i.putExtra("uid", uid);
            i.putExtra("password", password);
            startActivity(i);
        });

        // Configura el botón "Ver Solicitudes" para acceder al buzón de solicitudes
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