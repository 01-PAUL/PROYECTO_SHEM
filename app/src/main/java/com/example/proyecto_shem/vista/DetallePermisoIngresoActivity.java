package com.example.proyecto_shem.vista;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_shem.R;

public class DetallePermisoIngresoActivity extends AppCompatActivity {
    TextView detalleNomUsuario, detalleArea, detalleMotivo, detalleTipoMovilidad, detalleFechaIngreso, detalleHoraIngreso,tipoDocumento,numDocumento;
    ImageView detalleImagen;
    Button btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_permiso_ingreso);

        btnRegresar = findViewById(R.id.btnRegresar);
        detalleImagen = findViewById(R.id.detalleImagen);
        detalleNomUsuario = findViewById(R.id.detalleNomUsuario);
        detalleArea = findViewById(R.id.detalleArea);
        detalleMotivo = findViewById(R.id.detalleMotivo);
        tipoDocumento = findViewById(R.id.tipoDocumento);
        numDocumento = findViewById(R.id.numDocumento);
        detalleTipoMovilidad = findViewById(R.id.detalleTipoMovilidad);
        detalleFechaIngreso = findViewById(R.id.detalleFechaIngreso);
        detalleHoraIngreso = findViewById(R.id.detalleHoraIngreso);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            detalleNomUsuario.setText(bundle.getString("usuario"));
            tipoDocumento.setText(bundle.getString("tipoDocumento"));
            numDocumento.setText(bundle.getString("numeroDocumento"));
            detalleArea.setText(bundle.getString("area")); // Asegúrate de que coincide con el putExtra
            detalleMotivo.setText(bundle.getString("motivo"));
            detalleTipoMovilidad.setText(bundle.getString("tipoMicromovilidad"));
            detalleFechaIngreso.setText(bundle.getString("fechaIngreso"));
            detalleHoraIngreso.setText(bundle.getString("horaIngreso"));
            Glide.with(this).load(bundle.getString("imageUrl")).into(detalleImagen);
        }

        btnRegresar.setOnClickListener(v -> finish());
    }

}