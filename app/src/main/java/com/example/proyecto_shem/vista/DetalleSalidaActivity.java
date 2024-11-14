package com.example.proyecto_shem.vista;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.proyecto_shem.R;

public class DetalleSalidaActivity extends AppCompatActivity {

    TextView detalleTipoUsuario, detalleNomUsuario, detalleCodigoUsuario, detalleTipoDoc, detalleNumDoc, detalleMicromovilidad, detalleFechaSalida, detalleHoraSalida;
    ImageView detalleImagen;
    Button btnRegresar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_salida);

        btnRegresar = findViewById(R.id.btnRegresar);
        detalleTipoUsuario = findViewById(R.id.detalleTipoUsuario);
        detalleImagen = findViewById(R.id.detalleImagen);
        detalleNomUsuario = findViewById(R.id.detalleNomUsuario);
        detalleCodigoUsuario = findViewById(R.id.detalleCodigoUsuario);
        detalleTipoDoc = findViewById(R.id.detalleTipoDoc);
        detalleNumDoc = findViewById(R.id.detalleNumDoc);
        detalleMicromovilidad = findViewById(R.id.detalleTipoMovilidad);
        detalleFechaSalida = findViewById(R.id.detalleFechaSalida);
        detalleHoraSalida = findViewById(R.id.detalleHoraSalida);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            detalleTipoUsuario.setText(bundle.getString("tipoUsuario"));
            Glide.with(this).load(bundle.getString("imageUrl")).into(detalleImagen);
            detalleNomUsuario.setText(bundle.getString("usuario"));
            detalleCodigoUsuario.setText(bundle.getString("codigoUsuario"));
            detalleTipoDoc.setText(bundle.getString("tipoDocumento"));
            detalleNumDoc.setText(bundle.getString("numeroDocumento"));
            detalleMicromovilidad.setText(bundle.getString("micromovilidad"));
            detalleFechaSalida.setText(bundle.getString("fechaSalida"));
            detalleHoraSalida.setText(bundle.getString("horaSalida"));
        }

        btnRegresar.setOnClickListener(v -> finish());

    }
}