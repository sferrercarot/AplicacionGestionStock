package com.example.aplicaciongestionstockimprenta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";
    private Button btnVerStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Log.d(TAG, "onCreate: AdminActivity arrancado");

        // 1) Localiza el botón
        btnVerStock = findViewById(R.id.btnVerStock);
        if (btnVerStock == null) {
            Log.e(TAG, "¡Error! btnVerStock es null. ¿Coincide el id con el XML?");
            Toast.makeText(this, "Error interno: botón no encontrado", Toast.LENGTH_LONG).show();
            return;
        }

        // 2) Ligar listener
        btnVerStock.setOnClickListener(v -> {
            Log.d(TAG, "btnVerStock.onClick: lanzando StockListActivity");

            Intent i = new Intent(AdminActivity.this, StockListActivity.class);

            // Pasa los extras que realmente necesite StockListActivity
            int uid       = getIntent().getIntExtra("uid", -1);
            String usuario  = getIntent().getStringExtra("usuario");
            String password = getIntent().getStringExtra("password");
            String rol      = getIntent().getStringExtra("rol");
            String sessionId = getIntent().getStringExtra("sessionId");

            // Valida
            if (uid == -1 || usuario == null) {
                Log.w(TAG, "Datos de sesión inválidos: uid=" + uid + " usuario=" + usuario);
                Toast.makeText(this, "Datos de sesión inválidos", Toast.LENGTH_SHORT).show();
                return;
            }

            i.putExtra("uid",       uid);
            i.putExtra("usuario",   usuario);
            i.putExtra("password",  password);
            i.putExtra("rol",       rol);
            i.putExtra("sessionId", sessionId);

            startActivity(i);
        });
    }
}
