package com.example.aplicaciongestionstockimprenta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView mensajeTextView;
    private Button btnVerStock, btnActualizarStock, btnSolicitarMaterial;

    private String rol;
    private String user;
    private String password;
    private int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mensajeTextView = findViewById(R.id.mensajeTextView);
        btnVerStock = findViewById(R.id.btnVerStock);
        btnActualizarStock = findViewById(R.id.btnActualizarStock);
        btnSolicitarMaterial = findViewById(R.id.btnSolicitarMaterial);

        rol = getIntent().getStringExtra("rol");
        user = getIntent().getStringExtra("usuario");
        password = getIntent().getStringExtra("password");
        uid = getIntent().getIntExtra("uid", -1);

        actualizarMensajeDeBienvenida();  // <-- 🔥 aquí llamamos a tu nueva función limpia

        btnVerStock.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StockListActivity.class);
            intent.putExtra("usuario", user);
            intent.putExtra("password", password);
            intent.putExtra("uid", uid);
            startActivity(intent);
        });

        btnActualizarStock.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidad de actualizar stock pendiente", Toast.LENGTH_SHORT).show();
        });

        btnSolicitarMaterial.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidad de solicitar material pendiente", Toast.LENGTH_SHORT).show();
        });
    }

    private void actualizarMensajeDeBienvenida() {
        if (rol == null) {
            mensajeTextView.setText(R.string.mensaje_default);
            hideButtons();
        } else if ("admin".equals(rol)) {
            mensajeTextView.setText(getString(R.string.bienvenido_admin, user));
        } else if ("fabrica".equals(rol)) {
            mensajeTextView.setText(getString(R.string.bienvenido_fabrica, user));
            btnSolicitarMaterial.setVisibility(View.GONE);
        } else if ("contabilidad".equals(rol)) {
            mensajeTextView.setText(getString(R.string.bienvenido_contabilidad, user));
            btnActualizarStock.setVisibility(View.GONE);
        } else {
            mensajeTextView.setText(R.string.rol_no_reconocido);
            hideButtons();
        }
    }

    private void hideButtons() {
        btnVerStock.setVisibility(View.GONE);
        btnActualizarStock.setVisibility(View.GONE);
        btnSolicitarMaterial.setVisibility(View.GONE);
    }
}
