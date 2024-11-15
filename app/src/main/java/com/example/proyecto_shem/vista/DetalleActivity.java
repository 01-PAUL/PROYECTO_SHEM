package com.example.proyecto_shem.vista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.proyecto_shem.R;

public class DetalleActivity extends AppCompatActivity {

    TextView detalleTipoUsuario, detalleNomUsuario, detalleCodigoUsuario, detalleTipoDoc, detalleNumDoc, detalleTipoMovilidad, detalleFechaIngreso, detalleHoraIngreso;
    ImageView detalleImagen;
    Button btnHistorial, btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle);

        btnHistorial = findViewById(R.id.btnHistorial);
        btnRegresar = findViewById(R.id.btnRegresar);
        detalleTipoUsuario = findViewById(R.id.detalleTipoUsuario);
        detalleImagen = findViewById(R.id.detalleImagen);
        detalleNomUsuario = findViewById(R.id.detalleNomUsuario);
        detalleCodigoUsuario = findViewById(R.id.detalleCodigoUsuario);
        detalleTipoDoc = findViewById(R.id.detalleTipoDoc);
        detalleNumDoc = findViewById(R.id.detalleNumDoc);
        detalleTipoMovilidad = findViewById(R.id.detalleTipoMovilidad);
        detalleFechaIngreso = findViewById(R.id.detalleFechaIngreso);
        detalleHoraIngreso = findViewById(R.id.detalleHoraIngreso);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            detalleTipoUsuario.setText(bundle.getString("tipoUsuario"));
            Glide.with(this).load(bundle.getString("imageUrl")).into(detalleImagen);
            detalleNomUsuario.setText(bundle.getString("usuario"));
            detalleCodigoUsuario.setText(bundle.getString("codigoUsuario"));
            detalleTipoDoc.setText(bundle.getString("tipoDocumento"));
            detalleNumDoc.setText(bundle.getString("numeroDocumento"));
            detalleTipoMovilidad.setText(bundle.getString("tipoMicromovilidad"));
            detalleFechaIngreso.setText(bundle.getString("fechaIngreso"));
            detalleHoraIngreso.setText(bundle.getString("horaIngreso"));
        }

        btnRegresar.setOnClickListener(v -> finish());
        btnHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(DetalleActivity.this, HistorialIngresoUsuarioActivity.class);
            intent.putExtra("codigoUsuario", detalleCodigoUsuario.getText().toString());
            intent.putExtra("usuario", detalleNomUsuario.getText().toString());
            startActivity(intent);
            finish();
        });

    }
}