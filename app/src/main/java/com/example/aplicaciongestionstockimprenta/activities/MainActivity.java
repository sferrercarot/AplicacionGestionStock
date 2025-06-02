package com.example.aplicaciongestionstockimprenta.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aplicaciongestionstockimprenta.R;

public class MainActivity extends AppCompatActivity {

    private String rol, user, password;
    private int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rol = getIntent().getStringExtra("rol");
        user = getIntent().getStringExtra("usuario");
        password = getIntent().getStringExtra("password");
        uid = getIntent().getIntExtra("uid", -1);

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
                    intent = new Intent(this, LoginActivity.class);
                    break;
            }
            intent.putExtra("rol",       rol);
            intent.putExtra("usuario",   user);
            intent.putExtra("password",  password);
            intent.putExtra("uid",       uid);

            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
    }
}
