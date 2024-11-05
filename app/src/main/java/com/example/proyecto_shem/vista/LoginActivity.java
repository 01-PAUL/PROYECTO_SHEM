package com.example.proyecto_shem.vista;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_shem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    TextView olvidasteContrasena;
    Button btnLogin;
    EditText txtLogin, txtClave;
    ImageView eyeIcon;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtLogin = findViewById(R.id.txtLogin);
        txtClave = findViewById(R.id.txtClave);
        btnLogin = findViewById(R.id.btnLogin);
        olvidasteContrasena = findViewById(R.id.olvidasteContrasena);
        eyeIcon = findViewById(R.id.eyeIcon);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // Alternar visibilidad de contraseña
        eyeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Cambiar a ocultar contraseña
                    txtClave.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeIcon.setImageResource(R.drawable.ic_eye_closed); // Cambia al icono de ojo cerrado
                    isPasswordVisible = false;
                } else {
                    // Cambiar a mostrar contraseña
                    txtClave.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeIcon.setImageResource(R.drawable.ic_eye_open); // Cambia al icono de ojo abierto
                    isPasswordVisible = true;
                }
                // Mueve el cursor al final del texto
                txtClave.setSelection(txtClave.length());
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailUser = txtLogin.getText().toString().trim();
                String passUser = txtClave.getText().toString().trim();

                // Validación de los campos de email y contraseña
                if (emailUser.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor, ingrese el email", Toast.LENGTH_LONG).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailUser).matches()) {
                    // Validar si el email tiene el formato correcto
                    Toast.makeText(LoginActivity.this, "Ingrese un email válido", Toast.LENGTH_LONG).show();
                } else if (passUser.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor, ingrese la contraseña", Toast.LENGTH_LONG).show();
                } else {
                    // Si ambos campos están correctos, realizar la autenticación
                    mAuth.signInWithEmailAndPassword(emailUser, passUser)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Redirigir al MenuPrincipalActivity si el login es exitoso
                                        Intent intent = new Intent(LoginActivity.this, MenuPrincipalActivity.class);
                                        intent.putExtra("user_email", emailUser);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Si el login falla, mostrar mensaje de error
                                        Toast.makeText(LoginActivity.this, "Email y/o contraseña incorrectos", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        olvidasteContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RecuperarContrasenaActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


}