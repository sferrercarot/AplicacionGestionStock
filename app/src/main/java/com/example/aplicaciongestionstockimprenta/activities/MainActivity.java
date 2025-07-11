package com.example.aplicaciongestionstockimprenta.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aplicaciongestionstockimprenta.R;

// Esta clase actúa como punto de entrada común para redirigir al usuario
// a la actividad correspondiente según su rol (admin, fábrica, contabilidad, etc.)
public class MainActivity extends AppCompatActivity {

    // Variables para guardar los datos de sesión del usuario
    private String rol, user, password;
    private int uid;

    // Método que se ejecuta al crear la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recupera los datos de sesión enviados desde LoginActivity
        rol = getIntent().getStringExtra("rol");
        user = getIntent().getStringExtra("usuario");
        password = getIntent().getStringExtra("password");
        uid = getIntent().getIntExtra("uid", -1);

        // Si se recibió el rol, se redirige automáticamente al módulo correspondiente
        if (rol != null) {
            Intent intent;

            // Selecciona la actividad a lanzar según el rol
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
                    intent = new Intent(this, StockListActivity.class); // por si se accede directamente al stock
                    break;
                default:
                    intent = new Intent(this, LoginActivity.class); // si el rol no es reconocido, vuelve al login
                    break;
            }

            // Pasa todos los datos de sesión a la siguiente actividad
            intent.putExtra("rol", rol);
            intent.putExtra("usuario", user);
            intent.putExtra("password", password);
            intent.putExtra("uid", uid);

            // Lanza la actividad y finaliza esta para que no quede en la pila
            startActivity(intent);
            finish();
            return;
        }

        // Si no hay rol recibido, se carga un layout por defecto (opcional)
        setContentView(R.layout.activity_main);
    }
}