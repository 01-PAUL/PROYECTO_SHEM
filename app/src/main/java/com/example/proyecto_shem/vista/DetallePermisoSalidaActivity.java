package com.example.proyecto_shem.vista;


import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_shem.R;

public class DetallePermisoSalidaActivity extends AppCompatActivity {
    TextView detalleNomUsuario, detalleArea, detalleMotivo, detalleTipoMovilidad, detalleFechaSalida, detalleHoraSalida;
    ImageView detalleImagen;
    Button btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_permiso_salida);

        btnRegresar = findViewById(R.id.btnRegresar);
        detalleImagen = findViewById(R.id.detalleImagen);
        detalleNomUsuario = findViewById(R.id.detalleNomUsuario);
        detalleArea = findViewById(R.id.detalleArea);
        detalleMotivo = findViewById(R.id.detalleMotivo);
        detalleTipoMovilidad = findViewById(R.id.detalleTipoMovilidad);
        detalleFechaSalida = findViewById(R.id.detalleFechaSalida);
        detalleHoraSalida = findViewById(R.id.detalleHoraSalida);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            detalleNomUsuario.setText(bundle.getString("usuario"));
            detalleArea.setText(bundle.getString("area")); // Asegúrate de que coincide con el putExtra
            detalleMotivo.setText(bundle.getString("motivo"));
            detalleTipoMovilidad.setText(bundle.getString("micromovilidad"));
            detalleFechaSalida.setText(bundle.getString("fechaSalida"));
            detalleHoraSalida.setText(bundle.getString("horaSalida"));
            Glide.with(this).load(bundle.getString("imageUrl")).into(detalleImagen);
        }

        btnRegresar.setOnClickListener(v -> finish());
    }
}
