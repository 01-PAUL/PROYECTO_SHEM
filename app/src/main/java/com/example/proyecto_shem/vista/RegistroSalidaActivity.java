package com.example.proyecto_shem.vista;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_shem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistroSalidaActivity extends AppCompatActivity {

    TextView txtCodigoUsuario, txtNumeroDocumento, txtUsuario, txtMovilidad;
    Spinner spinnerTipoUsuario, spinnerTipoDocumento;
    Button btnEscanearQR, btnConsultar, btnSalida, btnRegresar;
    ImageView imgView;

    private DatabaseReference databaseReference;
    private Map<Integer, String> tipoUsuarioMap;
    private Map<Integer, String> tipoDocumentoMap;

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

        imgView = findViewById(R.id.imgView);

        // Referencia a Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Cargar los datos de Firebase en los spinners
        loadSpinnerData();

        // Acción al presionar el botón EscanearQR
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

        //Consultar por Codigo Usuario y Número de Documento
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()) {
                    String codUsuario = txtCodigoUsuario.getText().toString();
                    String numDocumento = txtNumeroDocumento.getText().toString();
                    if (!codUsuario.isEmpty()) {
                        consultarCodUsuario(codUsuario);
                    } else if (!numDocumento.isEmpty()) {
                        consultarNumDocumento(numDocumento);
                    } else {
                        Toast.makeText(RegistroSalidaActivity.this, "Ingrese el Codigo Usuario o N° Documento", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Acción al presionar el botón Salida
        btnSalida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    String codigoUsuario = txtCodigoUsuario.getText().toString();

                    // Verificar si el codigoUsuario ya está registrado en la tabla Salida
                    databaseReference.child("Salida")
                    .orderByChild("codigoUsuario")
                    .equalTo(codigoUsuario)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Si existe, mostrar mensaje de usuario ya registrado
                                Toast.makeText(RegistroSalidaActivity.this, "Usuario ya salió a Cibertec", Toast.LENGTH_SHORT).show();
                            } else {
                                // Si no existe, proceder con el registro de salida
                                String tipoUsuario = spinnerTipoUsuario.getSelectedItem().toString();
                                String tipoDocumento = spinnerTipoDocumento.getSelectedItem().toString();
                                String numeroDocumento = txtNumeroDocumento.getText().toString();
                                String usuario = txtUsuario.getText().toString();
                                String micromovilidad = txtMovilidad.getText().toString();
                                String imageUrl = imgView.getTag() != null ? imgView.getTag().toString() : "";

                                // Obtener la fecha y hora actuales
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

                                String fechaSalida = dateFormat.format(calendar.getTime());
                                String horaSalida = timeFormat.format(calendar.getTime());

                                Map<String, Object> datosSalida = new HashMap<>();
                                datosSalida.put("tipoUsuario", tipoUsuario);
                                datosSalida.put("codigoUsuario", codigoUsuario);
                                datosSalida.put("tipoDocumento", tipoDocumento);
                                datosSalida.put("numeroDocumento", numeroDocumento);
                                datosSalida.put("usuario", usuario);
                                datosSalida.put("micromovilidad", micromovilidad);
                                datosSalida.put("imageUrl", imageUrl);
                                datosSalida.put("fechaSalida", fechaSalida);
                                datosSalida.put("horaSalida", horaSalida);

                                databaseReference.child("Salida").push().setValue(datosSalida)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                showSuccessDialog(usuario, micromovilidad);
                                            } else {
                                                Toast.makeText(RegistroSalidaActivity.this, "Error al registrar salida", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(RegistroSalidaActivity.this, "Error al verificar código de usuario", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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

    // Método para cargar datos en los spinners desde Firebase
    private void loadSpinnerData() {
        loadSpinnerFromFirebase(databaseReference.child("tipoUsuario"), spinnerTipoUsuario);
        loadSpinnerFromFirebase(databaseReference.child("tipoDocumento"), spinnerTipoDocumento);
    }

    private void loadSpinnerFromFirebase(DatabaseReference ref, Spinner spinner) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> items = new ArrayList<>();
                items.add("Seleccione:");

                // Crear un mapa para almacenar ID y nombre
                Map<Integer, String> idNameMap = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    int id = Integer.parseInt(snapshot.getKey());
                    String name = snapshot.getValue(String.class);
                    items.add(name);
                    idNameMap.put(id, name);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(RegistroSalidaActivity.this, android.R.layout.simple_spinner_item, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                // Guardar el mapa en una variable para usarlo luego
                if (spinner == spinnerTipoUsuario) {
                    tipoUsuarioMap = idNameMap;
                } else if (spinner == spinnerTipoDocumento) {
                    tipoDocumentoMap = idNameMap;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RegistroSalidaActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //QR
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

                    // Extraer los campos del JSON, incluyendo codigoUsuario
                    String tipoUsuario = qrData.getString("tipoUsuario");
                    String codigoUsuario = qrData.getString("codigoUsuario");
                    String tipoDocumento = qrData.getString("tipoDocumento");
                    String numeroDocumento = qrData.getString("numeroDocumento");
                    String usuario = qrData.getString("usuario");
                    String imageUrl = qrData.getString("imageUrl");

                    // Asignar a los TextView y Spinners
                    txtCodigoUsuario.setText(codigoUsuario);
                    txtNumeroDocumento.setText(numeroDocumento);
                    txtUsuario.setText(usuario);
                    setSpinnerValue(spinnerTipoUsuario, tipoUsuario);
                    setSpinnerValue(spinnerTipoDocumento, tipoDocumento);

                    // Cargar la imagen en imgView usando Glide
                    Glide.with(this)
                            .load(imageUrl)
                            .into(imgView);

                    // Llamar a la función para obtener micromovilidad desde Firebase
                    fetchMicromovilidad(codigoUsuario);

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

    //Para el Consultar por CODIGO USUARIO
    private void consultarCodUsuario(String codUsuario) {
        String codUsuarioUpper = codUsuario.toUpperCase();
        // Primero, intenta buscar en la tabla "Ingreso"
        databaseReference.child("Ingreso").orderByChild("codigoUsuario").equalTo(codUsuarioUpper)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Si encuentra el usuario en "Ingreso", llena los datos
                            consultarCodUsuario(dataSnapshot);
                        } else {
                            // Si no encuentra al usuario, muestra el mensaje
                            Toast.makeText(RegistroSalidaActivity.this, "Usuario no ingreso a Cibertec", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroSalidaActivity.this, "Error al consultar el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Método para el CONSULTAR donde cargue los datos del usuario una vez encontrado
    private void consultarCodUsuario(DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            // Obtén el valor de tipoUsuario y tipoDocumento como string
            String tipoUsuario = getStringValue(snapshot, "tipoUsuario");
            String tipoDocumento = getStringValue(snapshot, "tipoDocumento");
            String numeroDocumento = getStringValue(snapshot, "numeroDocumento");
            String usuario = getStringValue(snapshot, "usuario");
            String imageUrl = getStringValue(snapshot, "imageUrl");
            String codigoUsuario = getStringValue(snapshot, "codigoUsuario");

            txtUsuario.setText(usuario);
            txtNumeroDocumento.setText(numeroDocumento != null ? numeroDocumento : "");

            // Selecciona el valor adecuado en el spinner
            setSpinnerValue(spinnerTipoUsuario, tipoUsuario);
            setSpinnerValue(spinnerTipoDocumento, tipoDocumento);

            // Cargar la imagen usando Glide
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(imgView);
                imgView.setTag(imageUrl);
            }

            // Llamar a la función para obtener micromovilidad desde Firebase
            if (codigoUsuario != null) {
                fetchMicromovilidad(codigoUsuario);
            }
        }
    }


    //Para el Consultar por NÚMERO DE DOCUMENTO
    private void consultarNumDocumento(String numDocumento) {
        // Primero, intenta buscar en la tabla "estudiante"
        databaseReference.child("Ingreso").orderByChild("numeroDocumento").equalTo(numDocumento)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Si encuentra el usuario en "estudiante", llena los datos
                            cargarDatosNumDocumento(dataSnapshot);
                        } else {
                            // Si no encuentra al usuario, muestra el mensaje
                            Toast.makeText(RegistroSalidaActivity.this, "Usuario no ingreso a Cibertec", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroSalidaActivity.this, "Error al consultar el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para el CONSULTAR donde cargue los datos del usuario una vez encontrado
    private void cargarDatosNumDocumento(DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            String tipoUsuario = getStringValue(snapshot, "tipoUsuario");
            String tipoDocumento = getStringValue(snapshot, "tipoDocumento");
            String usuario = getStringValue(snapshot, "usuario");
            String imageUrl = getStringValue(snapshot, "imageUrl");
            String codigoUsuario = getStringValue(snapshot, "codigoUsuario");

            txtUsuario.setText(usuario);
            txtCodigoUsuario.setText(codigoUsuario);
            // Selecciona el valor adecuado en el spinner
            setSpinnerValue(spinnerTipoUsuario, tipoUsuario);
            setSpinnerValue(spinnerTipoDocumento, tipoDocumento);

            // Cargar la imagen usando Glide
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(imgView);
                imgView.setTag(imageUrl);
            }

            // Llamar a la función para obtener micromovilidad desde Firebase
            if (codigoUsuario != null) {
                fetchMicromovilidad(codigoUsuario);
            }
        }
    }


    private String getStringValue(DataSnapshot snapshot, String key) {
        Object value = snapshot.child(key).getValue();
        return value != null ? value.toString() : null;
    }

    // Función para obtener el dato de micromovilidad desde Firebase usando codigoUsuario
    private void fetchMicromovilidad(String codigoUsuario) {
        DatabaseReference ingresoRef = databaseReference.child("Ingreso");
        ingresoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String codigo = childSnapshot.child("codigoUsuario").getValue(String.class);

                    if (codigo != null && codigo.equals(codigoUsuario)) {
                        String micromovilidad = childSnapshot.child("tipoMicromovilidad").getValue(String.class);
                        txtMovilidad.setText(micromovilidad != null ? micromovilidad : "");
                        found = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RegistroSalidaActivity.this, "Error al consultar micromovilidad", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // VALIDACIONES
    private boolean validateInputs() {
        String numDocumento = txtNumeroDocumento.getText().toString();
        if (!numDocumento.isEmpty()) {
            if (numDocumento.length() != 8 || !numDocumento.matches("\\d+")) {
                Toast.makeText(this, "El número de documento debe tener 8 dígitos", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (spinnerTipoUsuario.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Ingrese datos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (txtCodigoUsuario.getText().toString().isEmpty()) {
            Toast.makeText(this, "Ingrese el código de usuario", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spinnerTipoDocumento.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione un tipo de documento", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (txtNumeroDocumento.getText().toString().isEmpty()) {
            Toast.makeText(this, "Ingrese el número de documento", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (txtUsuario.getText().toString().isEmpty()) {
            Toast.makeText(this, "Ingrese el nombre de usuario", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txtMovilidad.getText().toString().isEmpty()) {
            Toast.makeText(this, "Usuario no ingreso a Cibertec", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validarCampos() {
        String numDocumento = txtNumeroDocumento.getText().toString();
        if (!numDocumento.isEmpty()) {
            if (numDocumento.length() != 8 || !numDocumento.matches("\\d+")) {
                Toast.makeText(this, "El número de documento debe tener 8 dígitos", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private void setSpinnerValue(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int position = adapter.getPosition(value); // Encuentra la posición del valor en el adaptador
        if (position >= 0) {
            spinner.setSelection(position); // Establece la posición en el spinner
        }
    }

    //MENSAJE DE SALIDA
    private void showSuccessDialog(String usuario, String tipoMicromovilidad) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SALIDA REGISTRADA DE FORMA EXITOSA!!!!");
        builder.setMessage("SALIÓ DE CIBERTEC\n" + usuario + "\n" +
                        "TIPO DE MICROMOVILIDAD: " + tipoMicromovilidad)
                .setPositiveButton("Aceptar", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
        clearInputs();
    }

    //LIMPIAR LOS CAMPOS LUEGO DE REGISTRAR LA SALIDA
    private void clearInputs() {
        txtCodigoUsuario.setText("");
        txtNumeroDocumento.setText("");
        txtUsuario.setText("");
        txtMovilidad.setText("");
        spinnerTipoUsuario.setSelection(0);
        spinnerTipoDocumento.setSelection(0);
        imgView.setImageResource(0);
    }

}