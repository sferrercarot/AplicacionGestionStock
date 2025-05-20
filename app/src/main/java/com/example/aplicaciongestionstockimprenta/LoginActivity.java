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
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.aplicaciongestionstockimprenta.RolResponse;


public class LoginActivity extends AppCompatActivity {

    private EditText usuarioEt, passwordEt;
    private Button loginBtn;
    private ProgressBar progress;
    private OdooService service;

    private final String DB_NAME = "gestion_almacen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuarioEt  = findViewById(R.id.etEmail);
        passwordEt = findViewById(R.id.etPassword);
        loginBtn   = findViewById(R.id.btnLogin);
        progress   = findViewById(R.id.progressBar);

        // 🔍 Interceptor de logs
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(msg -> Log.d("HTTP", msg));
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // ✅ Cliente con logging y cookies compartidas
        OkHttpClient client = RetrofitClient.getHttpClientWithLogging(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://50.85.209.163:8069/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(OdooService.class);

        loginBtn.setOnClickListener(view -> {
            String usuario = usuarioEt.getText().toString().trim();
            String password = passwordEt.getText().toString().trim();

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            progress.setVisibility(View.VISIBLE);
            loginBtn.setEnabled(false);

            // 📦 Cuerpo de la solicitud JSON-RPC
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

                        if (uid <= 0) {
                            Log.e("LOGIN", "❌ Login fallido. UID inválido: " + uid);
                            Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Log.d("LOGIN", "✅ Login correcto. UID: " + uid);

                        JsonObject emptyBody = new JsonObject();
                        service.obtenerRol(emptyBody).enqueue(new Callback<RolResponse>() {
                            @Override
                            public void onResponse(Call<RolResponse> call, Response<RolResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    RolResponse rolResp = response.body();

                                    String rol = rolResp.result.rol;
                                    Intent i;

                                    if (rol == null) {
                                        Toast.makeText(LoginActivity.this, "Error: rol es null", Toast.LENGTH_SHORT).show();
                                        Log.e("LOGIN", "❌ Rol null en respuesta: " + new Gson().toJson(rolResp));
                                        return;
                                    }

                                    Log.d("LOGIN", "✅ Rol recibido: " + rol);

                                    switch (rol) {
                                        case "admin":
                                            i = new Intent(LoginActivity.this, AdminActivity.class);
                                            break;
                                        case "fabrica":
                                            i = new Intent(LoginActivity.this, FabricaActivity.class);
                                            break;
                                        case "contabilidad":
                                            i = new Intent(LoginActivity.this, ContabilidadActivity.class);
                                            break;
                                        default:
                                            Toast.makeText(LoginActivity.this, "Rol desconocido: " + rol, Toast.LENGTH_SHORT).show();
                                            return;
                                    }

                                    i.putExtra("uid", uid);
                                    i.putExtra("usuario", usuario);
                                    i.putExtra("password", password);
                                    i.putExtra("rol", rolResp.result.rol);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Error al obtener rol del usuario", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<RolResponse> call, Throwable t) {
                                Toast.makeText(LoginActivity.this, "Fallo al obtener rol: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Log.e("LOGIN", "❌ Login fallido. HTTP: " + resp.code());
                        Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Authenticate> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    loginBtn.setEnabled(true);
                    Log.e("LOGIN", "❌ Error de red al hacer login", t);
                    Toast.makeText(LoginActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
