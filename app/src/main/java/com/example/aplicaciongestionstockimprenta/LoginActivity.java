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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEt, passEt;
    private Button loginBtn;
    private ProgressBar progress;
    private OdooService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEt = findViewById(R.id.emailLoginEditText);
        passEt = findViewById(R.id.passwordLoginEditText);
        loginBtn = findViewById(R.id.loginButton);
        progress = findViewById(R.id.progressBar);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://50.85.209.163:8069/")
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(OdooService.class);

        loginBtn.setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        progress.setVisibility(View.VISIBLE);
        loginBtn.setEnabled(false);

        String db = "gestion_almacen";
        String user = emailEt.getText().toString().trim();
        String pass = passEt.getText().toString().trim();

        JsonObject loginBody = OdooRequestBuilder.buildLoginRequest(db, user, pass);
        Log.d("Login", "JSON enviado: " + loginBody.toString());

        service.login(loginBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject json = response.body();
                        Log.d("Login", "Respuesta cruda: " + json.toString());

                        if (json.has("result") && json.get("result").isJsonPrimitive()) {
                            int uid = json.get("result").getAsInt();

                            if (uid > 0) {
                                Log.d("Login", "UID recibido: " + uid);

                                JsonObject groupRequest = OdooRequestBuilder.buildUserGroupsRequest(db, uid, pass, uid);
                                Log.d("Login", "JSON grupos enviado: " + groupRequest.toString());

                                Call<JsonObject> groupCall = service.callJsonRpc(groupRequest);
                                groupCall.enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                        try {
                                            if (!response.isSuccessful() || response.body() == null) {
                                                Toast.makeText(LoginActivity.this, "Error al obtener grupos", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            JsonObject json = response.body();
                                            Log.d("GrupoRespuestaRaw", json.toString());

                                            String rol = "desconocido";
                                            if (json.has("result")) {
                                                JsonArray groups = json.getAsJsonArray("result")
                                                        .get(0).getAsJsonObject()
                                                        .getAsJsonArray("groups_id");

                                                for (JsonElement g : groups) {
                                                    int groupId = g.getAsInt();
                                                    if (groupId == 13) {
                                                        rol = "admin";
                                                        break;
                                                    } else if (groupId == 14) {
                                                        rol = "fabrica";
                                                        break;
                                                    } else if (groupId == 15) {
                                                        rol = "contabilidad";
                                                        break;
                                                    }
                                                }
                                            }

                                            Intent i;
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
                                                    Toast.makeText(LoginActivity.this, "Rol no reconocido", Toast.LENGTH_LONG).show();
                                                    return;
                                            }

                                            i.putExtra("uid", uid);
                                            i.putExtra("password", pass);
                                            i.putExtra("usuario", user);
                                            i.putExtra("rol", rol);
                                            startActivity(i);
                                            finish();
                                        } catch (Exception e) {
                                            Log.e("Login", "Error al procesar grupos", e);
                                            Toast.makeText(LoginActivity.this, "Error procesando grupos", Toast.LENGTH_SHORT).show();
                                        } finally {
                                            progress.setVisibility(View.GONE);
                                            loginBtn.setEnabled(true);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<JsonObject> call, Throwable t) {
                                        Log.e("Login", "Fallo total en llamada a grupos", t);
                                        Toast.makeText(LoginActivity.this, "Error de red al obtener grupos", Toast.LENGTH_SHORT).show();
                                        progress.setVisibility(View.GONE);
                                        loginBtn.setEnabled(true);
                                    }
                                });
                                return;
                            }
                        }
                        Toast.makeText(LoginActivity.this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login fallido", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("Login", "Error al procesar respuesta", e);
                    Toast.makeText(LoginActivity.this, "Error inesperado", Toast.LENGTH_SHORT).show();
                } finally {
                    progress.setVisibility(View.GONE);
                    loginBtn.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progress.setVisibility(View.GONE);
                loginBtn.setEnabled(true);
                Log.e("Login", "Error de red", t);
                Toast.makeText(LoginActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
