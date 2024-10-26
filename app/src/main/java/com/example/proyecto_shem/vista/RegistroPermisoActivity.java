package com.example.proyecto_shem.vista;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_shem.R;

public class RegistroPermisoActivity extends AppCompatActivity {

    TextView txtNumeroDocumento, txtUsuario, txtGenero, txtFechNacim;
    Spinner spinnerTipoDocumento, spinnerMicromovilidad;
    Button btnConsultar, btnRegistrar, btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_permiso);

        txtNumeroDocumento = findViewById(R.id.txtNumeroDocumento);
        txtUsuario = findViewById(R.id.txtUsuario);
        txtGenero = findViewById(R.id.txtGenero);
        txtFechNacim = findViewById(R.id.txtFechNacim);

        spinnerTipoDocumento = findViewById(R.id.spinnerTipoDocumento);
        spinnerMicromovilidad = findViewById(R.id.spinnerMicromovilidad);

        btnConsultar = findViewById(R.id.btnConsultar);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegresar = findViewById(R.id.btnRegresar);

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
