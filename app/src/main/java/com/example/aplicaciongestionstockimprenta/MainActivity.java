package com.example.aplicaciongestionstockimprenta;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String rol, user, password;
    private int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1) Recoges los extras del Intent (rol, user, etc)
        rol      = getIntent().getStringExtra("rol");
        user     = getIntent().getStringExtra("usuario");
        password = getIntent().getStringExtra("password");
        uid      = getIntent().getIntExtra("uid", -1);

        // 2) Si ya tienes rol, arranca la Activity adecuada y termina ésta
        if (rol != null) {
            Intent intent;
            switch (rol) {
                case "admin":
                    intent = new Intent(this, AdminActivity.class);
                    break;
                case "contabilidad":
                    intent = new Intent(this, ContabilidadActivity.class);
                    break;
                case "fabrica":
                    intent = new Intent(this, FabricaActivity.class);
                    break;
                case "stock":
                    intent = new Intent(this, StockListActivity.class);
                    break;
                default:
                    // rol desconocido → de vuelta al Login
                    intent = new Intent(this, LoginActivity.class);
                    break;
            }
            // pásales todos los extras que necesiten
            intent.putExtra("rol",       rol);
            intent.putExtra("usuario",   user);
            intent.putExtra("password",  password);
            intent.putExtra("uid",       uid);

            startActivity(intent);
            finish();   // destruyo la MainActivity para que no vuelva al “cargando”
            return;     // ¡muy importante!
        }

        // 3) Si no hay rol (por algún motivo), mostramos pantalla de carga
        setContentView(R.layout.activity_main);
        // aquí podrías implementar un “loading” mientras esperas al callback de nucleus/Odoo
    }
}
