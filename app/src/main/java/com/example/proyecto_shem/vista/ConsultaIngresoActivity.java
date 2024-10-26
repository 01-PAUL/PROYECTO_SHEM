package com.example.proyecto_shem.vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_shem.R;

public class ConsultaIngresoActivity extends AppCompatActivity {

    EditText txtCodUsuario, txtNombreUsuario;
    Button btnConsultar, btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_ingreso);

        btnConsultar = findViewById(R.id.btnConsultar);
        btnRegresar = findViewById(R.id.btnRegresar);
        txtCodUsuario = findViewById(R.id.txtCodUsuario);
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);

        // Acción al presionar el botón Regresar
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simplemente cerramos la actividad actual y regresamos a la anterior
                finish();
            }
        });

    }
}