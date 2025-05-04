package com.example.aplicaciongestionstockimprenta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    private Button verStockBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Log.d("AdminActivity", "Estoy en AdminActivity");

        verStockBtn = findViewById(R.id.btnVerStock); // asegúrate que el ID del botón en el XML es este

        verStockBtn.setOnClickListener(v -> {
            Intent i = new Intent(AdminActivity.this, StockListActivity.class);
            i.putExtra("uid", getIntent().getIntExtra("uid", -1));
            i.putExtra("password", getIntent().getStringExtra("password"));
            i.putExtra("usuario", getIntent().getStringExtra("usuario"));
            i.putExtra("rol", getIntent().getStringExtra("rol"));
            startActivity(i);
        });
    }
}
