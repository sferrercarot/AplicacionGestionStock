//package com.example.aplicaciongestionstockimprenta;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.*;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.auth.*;
//import com.google.firebase.firestore.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class RegistroActivity extends AppCompatActivity {
//
//    private EditText emailEditText, passwordEditText;
//    private Spinner rolSpinner;
//    private Button registroButton;
//
//    private FirebaseAuth mAuth;
//    private FirebaseFirestore db;
//
//    private static final String TAG = "RegistroActivity";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_registros);
//
//        /* Activamos Firebase para esta pantalla e instanciamos mAuth
//        para autenticar y db para guardar datos */
//        FirebaseApp.initializeApp(this);
//        mAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//
//        /* Iniciamos unas variables que conectamos con los elementos visuales
//        que hemos definido en el xml */
//        emailEditText = findViewById(R.id.emailEditText);
//        passwordEditText = findViewById(R.id.passwordEditText);
//        rolSpinner = findViewById(R.id.rolSpinner);
//        registroButton = findViewById(R.id.registroButton);
//
//        /* Pillamos del strings.xml los roles que hemos definido que queremos
//        que hayan disponibles en el spinner */
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.roles_array, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        rolSpinner.setAdapter(adapter);
//
//
//        //Cuando havemos click en el botón llamamos a registrarUsuario()
//        registroButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                registrarUsuario();
//            }
//        });
//    }
//
//    private void registrarUsuario() {
//        //Leemos lo que el usuario ha seleccionado
//        String email = emailEditText.getText().toString();
//        String password = passwordEditText.getText().toString();
//        String rol = rolSpinner.getSelectedItem().toString();
//
//        /* Si el email o la contraseña están vacíos mostramos mensaje
//        de incompleto y salimos de la función */
//        if (email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        //Creamos el usuario en el Firebase y si va todo bien lo guardamos
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        String uid = mAuth.getCurrentUser().getUid();
//
//                        //Creamos un HashMap con la info del usuario
//                        Map<String, Object> userMap = new HashMap<>();
//                        userMap.put("uid", uid);
//                        userMap.put("email", email);
//                        userMap.put("rol", rol);
//
//                        //Guardamos el HashMap en usuarios de Firestore y usamos el UID como ID del documento
//                        db.collection("usuarios").document(uid).set(userMap)
//                                //Si todo ha salido bien mostramos mensaje de éxito y vamos a la página principal
//                                .addOnSuccessListener(aVoid -> {
//                                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
//                                    intent.putExtra("rol", rol); //Metemos el rol en la mochila para poder llevarnoslo a otra pantalla (MainActivity)
//                                    startActivity(intent);
//                                    finish();
//                                })
//
//                                //Algo ha fallado (correo o contraseña)
//                                .addOnFailureListener(e -> {
//                                    Log.e(TAG, "Error al guardar en Firestore", e);
//                                    Toast.makeText(this, "Error al guardar usuario", Toast.LENGTH_SHORT).show();
//                                });
//                        //Algo ha falllado con la conexión
//                    } else {
//                        Log.e(TAG, "Error al registrar usuario", task.getException());
//                        Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//}