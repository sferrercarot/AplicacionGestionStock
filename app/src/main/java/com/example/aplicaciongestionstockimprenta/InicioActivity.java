package com.example.aplicaciongestionstockimprenta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class InicioActivity extends AppCompatActivity {

    private Button loginButton;
    //private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        loginButton = findViewById(R.id.loginButton);
        //registerButton = findViewById(R.id.registerButton);

        // 🔁 Prueba: abrir TestActivity en lugar de LoginActivity
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        //registerButton.setOnClickListener(v -> {
        //    Intent intent = new Intent(InicioActivity.this, RegistroActivity.class);
        //    startActivity(intent);
        //});
    }
}
