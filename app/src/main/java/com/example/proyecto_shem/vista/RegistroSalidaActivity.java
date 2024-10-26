package com.example.proyecto_shem.vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_shem.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistroSalidaActivity extends AppCompatActivity {

    TextView txtCodigoUsuario, txtNumeroDocumento, txtUsuario, txtMovilidad;
    Spinner spinnerTipoUsuario, spinnerTipoDocumento;
    Button btnEscanearQR, btnConsultar, btnSalida, btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_salida);

        txtCodigoUsuario = findViewById(R.id.txtCodigoUsuario);
        txtNumeroDocumento = findViewById(R.id.txtNumeroDocumento);
        txtUsuario = findViewById(R.id.txtUsuario);
        txtMovilidad = findViewById(R.id.txtMovilidad);

        spinnerTipoUsuario = findViewById(R.id.spinnerTipoUsuario);
        spinnerTipoDocumento = findViewById(R.id.spinnerTipoDocumento);

        btnEscanearQR = findViewById(R.id.btnEscanearQR);
        btnConsultar = findViewById(R.id.btnConsultar);
        btnSalida = findViewById(R.id.btnSalida);
        btnRegresar = findViewById(R.id.btnRegresar);

        btnEscanearQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator integrador = new IntentIntegrator(RegistroSalidaActivity.this);
                integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrador.setPrompt("lector - CDP");
                integrador.setCameraId(0);
                integrador.setBeepEnabled(true);
                integrador.setBarcodeImageEnabled(true);
                integrador.initiateScan();
            }
        });

        // Acción al presionar el botón Regresar
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simplemente cerramos la actividad actual y regresamos a la anterior
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this,"Lectura cancelada...", Toast.LENGTH_LONG).show();
            } else {
                // Asumiendo que el contenido del QR es un JSON con los datos necesarios
                String qrContent = result.getContents();
                try {
                    JSONObject qrData = new JSONObject(qrContent);

                    // Extraer cada campo del JSON
                    String tipoUsuario = qrData.getString("tipoUsuario");
                    String codigoUsuario = qrData.getString("codigoUsuario");
                    String tipoDocumento = qrData.getString("tipoDocumento");
                    String numeroDocumento = qrData.getString("numeroDocumento");
                    String usuario = qrData.getString("usuario");
                    String movilidad = qrData.getString("movilidad");

                    // Asignar a los TextView
                    txtCodigoUsuario.setText(codigoUsuario);
                    txtNumeroDocumento.setText(numeroDocumento);
                    txtUsuario.setText(usuario);
                    txtMovilidad.setText(movilidad);

                    // Asignar a los Spinners (suponiendo que ya contienen estos valores)
                    setSpinnerValue(spinnerTipoUsuario, tipoUsuario);
                    setSpinnerValue(spinnerTipoDocumento, tipoDocumento);

                    Toast.makeText(this, "Datos leídos correctamente", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al leer los datos del QR", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    private void setSpinnerValue(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            String spinnerValue = spinner.getItemAtPosition(i).toString().trim();
            if (spinnerValue.equalsIgnoreCase(value.trim())) {
                spinner.setSelection(i);
                break;
            }
        }
    }

}