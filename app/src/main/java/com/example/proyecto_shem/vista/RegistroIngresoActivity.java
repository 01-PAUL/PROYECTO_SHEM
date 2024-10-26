package com.example.proyecto_shem.vista;

import android.media.Image;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_shem.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistroIngresoActivity extends AppCompatActivity {

    TextView txtCodigoUsuario, txtNumeroDocumento, txtUsuario, txtAutorizacion;
    Spinner spinnerTipoUsuario, spinnerTipoDocumento, spinnerMicromovilidad;
    Button btnEscanearQR, btnConsultar, btnIngreso, btnRegresar;
    ImageView imgView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_ingreso);

        txtCodigoUsuario = findViewById(R.id.txtCodigoUsuario);
        txtNumeroDocumento = findViewById(R.id.txtNumeroDocumento);
        txtUsuario = findViewById(R.id.txtUsuario);
        txtAutorizacion = findViewById(R.id.txtAutorizacion);

        spinnerTipoUsuario = findViewById(R.id.spinnerTipoUsuario);
        spinnerTipoDocumento = findViewById(R.id.spinnerTipoDocumento);
        spinnerMicromovilidad = findViewById(R.id.spinnerMicromovilidad);

        btnEscanearQR = findViewById(R.id.btnEscanearQR);
        btnConsultar = findViewById(R.id.btnConsultar);
        btnIngreso = findViewById(R.id.btnIngreso);
        btnRegresar = findViewById(R.id.btnRegresar);

        imgView = findViewById(R.id.imgView);

        btnEscanearQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator integrador = new IntentIntegrator(RegistroIngresoActivity.this);
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

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Lectura cancelada...", Toast.LENGTH_LONG).show();
            } else {
                String qrContent = result.getContents();
                try {
                    JSONObject qrData = new JSONObject(qrContent);

                    // Extraer los campos del JSON, incluyendo imageUrl
                    String tipoUsuario = qrData.getString("tipoUsuario");
                    String codigoUsuario = qrData.getString("codigoUsuario");
                    String tipoDocumento = qrData.getString("tipoDocumento");
                    String numeroDocumento = qrData.getString("numeroDocumento");
                    String usuario = qrData.getString("usuario");
                    String autorizacion = qrData.getString("autorizacion");
                    String imageUrl = qrData.getString("imageUrl");

                    // Asignar a los TextView y Spinners
                    txtCodigoUsuario.setText(codigoUsuario);
                    txtNumeroDocumento.setText(numeroDocumento);
                    txtUsuario.setText(usuario);
                    txtAutorizacion.setText(autorizacion);
                    setSpinnerValue(spinnerTipoUsuario, tipoUsuario);
                    setSpinnerValue(spinnerTipoDocumento, tipoDocumento);

                    // Cargar la imagen en imgView usando Glide
                    Glide.with(this)
                            .load(imageUrl)
                            .into(imgView);

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