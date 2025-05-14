package com.example.aplicaciongestionstockimprenta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.domatix.yevbes.nucleus.core.entities.session.authenticate.Authenticate;
import com.domatix.yevbes.nucleus.core.entities.session.authenticate.AuthenticateParams;
import com.domatix.yevbes.nucleus.core.entities.session.authenticate.AuthenticateReqBody;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText usuarioEt, passwordEt;
    private Button loginBtn;
    private ProgressBar progress;
    private OdooService service;
    private final String BASE_URL = "http://50.85.209.163:8069/web/session/";
    private final String DB_NAME  = "gestion_almacen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuarioEt  = findViewById(R.id.emailLoginEditText);
        passwordEt = findViewById(R.id.passwordLoginEditText);
        loginBtn   = findViewById(R.id.loginButton);
        progress   = findViewById(R.id.progressBar);

        // Interceptor de logging
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(msg -> Log.d("HTTP", msg));
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Cliente HTTP sin cookies
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(1900, TimeUnit.SECONDS)
                .readTimeout(1900, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(OdooService.class);

        loginBtn.setOnClickListener(view -> {
            String usuario  = usuarioEt.getText().toString().trim();
            String password = passwordEt.getText().toString().trim();

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            progress.setVisibility(View.VISIBLE);
            loginBtn.setEnabled(false);

            // Construye petición JSON-RPC
            AuthenticateParams params = new AuthenticateParams(DB_NAME, usuario, password);
            AuthenticateReqBody request = new AuthenticateReqBody();
            request.setParams(params);

            Log.d("LOGIN", "🔐 Enviando login por JSON-RPC: " + usuario);
            service.login(request).enqueue(new Callback<Authenticate>() {
                @Override
                public void onResponse(Call<Authenticate> call, Response<Authenticate> resp) {
                    progress.setVisibility(View.GONE);
                    loginBtn.setEnabled(true);

                    if (resp.isSuccessful() && resp.body() != null && resp.body().getResult() != null) {
                        int uid = resp.body().getResult().getUid();

                        // ⚠️ Validamos que uid sea válido (> 0)
                        if (uid <= 0) {
                            Log.e("LOGIN", "❌ Login fallido. UID inválido: " + uid);
                            Toast.makeText(LoginActivity.this,
                                    "Usuario o contraseña incorrectos",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Log.d("LOGIN", "✅ Login correcto. UID: " + uid);

                        // Salta directamente a MainActivity indicando rol "admin" por ahora
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.putExtra("uid",      uid);
                        i.putExtra("usuario",  usuario);
                        i.putExtra("password", password);
                        i.putExtra("rol",      "admin");  // ← temporal
                        startActivity(i);
                        finish();

                    } else {
                        Log.e("LOGIN", "❌ Login fallido. HTTP: " + resp.code());
                        Toast.makeText(LoginActivity.this,
                                "Usuario o contraseña incorrectos",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Authenticate> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    loginBtn.setEnabled(true);
                    Log.e("LOGIN", "❌ Error de red al hacer login", t);
                    Toast.makeText(LoginActivity.this,
                            "Error de red: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}