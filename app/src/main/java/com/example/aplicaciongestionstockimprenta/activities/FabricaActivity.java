package com.example.aplicaciongestionstockimprenta.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aplicaciongestionstockimprenta.R;

public class FabricaActivity extends AppCompatActivity {

    private static final String TAG = "FabricaActivity";

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
        // Se reutiliza el mismo layout que Admin y Contabilidad
        setContentView(R.layout.activity_admin);

        // Mensaje en el log para confirmar que la actividad se ha lanzado
        Log.d(TAG, "FabricaActivity arrancado");

        // Recupera los datos de sesión del intent
        uid = getIntent().getIntExtra("uid", -1);
        usuario = getIntent().getStringExtra("usuario");
        password = getIntent().getStringExtra("password");
        rol = getIntent().getStringExtra("rol");
        sessionId = getIntent().getStringExtra("sessionId");

        // Verifica que los datos de sesión son válidos y que el rol es 'fabrica'
        if (uid == -1 || usuario == null || password == null || !"fabrica".equals(rol)) {
            Toast.makeText(this, "Acceso denegado", Toast.LENGTH_LONG).show();
            finish(); // Cierra la actividad si hay un error
            return;
        }

        // Vincula los botones del layout con las variables
        btnVerStock = findViewById(R.id.btnVerStock);
        btnSolicitarMaterial = findViewById(R.id.btnSolicitarMaterial);
        btnVerSolicitudes = findViewById(R.id.btnVerSolicitudes);

        // Desactiva y oculta el botón de ver solicitudes (solo accesible para contabilidad)
        btnVerSolicitudes.setEnabled(false);
        btnVerSolicitudes.setVisibility(View.GONE);

        // Acción del botón "Ver Stock": abre la pantalla con la lista de productos
        btnVerStock.setOnClickListener(v -> {
            Log.d(TAG, "Lanzando StockListActivity");

            Intent i = new Intent(FabricaActivity.this, StockListActivity.class);
            i.putExtra("uid", uid);
            i.putExtra("usuario", usuario);
            i.putExtra("password", password);
            i.putExtra("rol", rol);
            i.putExtra("sessionId", sessionId);
            startActivity(i);
        });

        // Acción del botón "Solicitar Material": abre el formulario para enviar solicitudes
        btnSolicitarMaterial.setOnClickListener(v -> {
            Log.d(TAG, "Lanzando SolicitudMaterialActivity");

            Intent i = new Intent(FabricaActivity.this, SolicitudMaterialActivity.class);
            i.putExtra("uid", uid);
            i.putExtra("password", password);
            startActivity(i);
        });
    }
}