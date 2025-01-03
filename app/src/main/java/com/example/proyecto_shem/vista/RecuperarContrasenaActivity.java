package com.example.proyecto_shem.vista;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_shem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class RecuperarContrasenaActivity extends AppCompatActivity {

    Button btnRecuperar;
    EditText txtLogin;
    TextView atras;

    // Lista de correos permitidos para recuperar la contraseña
    private final List<String> allowedEmails = Arrays.asList(
            "paulponcehuaranga@gmail.com",
            "mmayllaquispe@gmail.com",
            "estiben.mt123@gmail.com",
            "jarencesar127@gmail.com"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        btnRecuperar = findViewById(R.id.btnRecuperar);
        txtLogin = findViewById(R.id.txtLogin);
        atras = findViewById(R.id.atras);

        btnRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecuperarContrasenaActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Validación del correo
    public void validate() {
        String email = txtLogin.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtLogin.setError("Correo inválido");
            return;
        }

        // Verifica si el correo está en la lista de correos permitidos
        if (!allowedEmails.contains(email)) {
            Toast.makeText(RecuperarContrasenaActivity.this, "Este correo no está autorizado para recuperar la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Procede a enviar el correo si la validación básica fue exitosa
        sendEmail(email);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(RecuperarContrasenaActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Método para enviar el correo de recuperación
    public void sendEmail(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RecuperarContrasenaActivity.this, "Correo enviado!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RecuperarContrasenaActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RecuperarContrasenaActivity.this, "Error al enviar correo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
