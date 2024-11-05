package com.example.proyecto_shem.vista;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.proyecto_shem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistroPermisoSalidaActivity extends AppCompatActivity {

    TextView txtNumeroDocumento, txtUsuario, txtDetalle, txtMicromovilidad;
    Spinner spinnerTipoDocumento;
    Button btnConsultar, btnRegistrar, btnRegresar;
    ImageView imgView;

    private DatabaseReference databaseReference;
    private Map<Integer, String> tipoDocumentoMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_permiso_salida);

        txtNumeroDocumento = findViewById(R.id.txtNumeroDocumento);
        txtUsuario = findViewById(R.id.txtUsuario);
        txtDetalle = findViewById(R.id.txtDetalle);
        txtMicromovilidad = findViewById(R.id.txtMicromovilidad);

        spinnerTipoDocumento = findViewById(R.id.spinnerTipoDocumento);

        btnConsultar = findViewById(R.id.btnConsultar);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegresar = findViewById(R.id.btnRegresar);

        imgView = findViewById(R.id.imgView);

        // Inicializar Firebase y Mapas
        databaseReference = FirebaseDatabase.getInstance().getReference();
        tipoDocumentoMap = new HashMap<>();

        // Cargar datos en los spinners
        loadSpinnerData();

        // Configuración de botones
        btnRegresar.setOnClickListener(v -> finish());

        btnConsultar.setOnClickListener(v -> {
            if (validarCampos()) {
                String numDocumento = txtNumeroDocumento.getText().toString();
                if (!numDocumento.isEmpty()) {
                    consultarNumDocumento(numDocumento);
                } else {
                    Toast.makeText(RegistroPermisoSalidaActivity.this, "Ingrese el N° Documento", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Acción al presionar el botón Salida
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    String numDocumento = txtNumeroDocumento.getText().toString();

                    // Verificar si el codigoUsuario ya está registrado en la tabla Salida
                    databaseReference.child("Permiso Salida")
                            .orderByChild("numDocumento")
                            .equalTo(numDocumento)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // Si existe, mostrar mensaje de usuario ya registrado
                                        Toast.makeText(RegistroPermisoSalidaActivity.this, "Usuario ya salió a Cibertec", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Si no existe, proceder con el registro de salida
                                        String tipoDocumento = spinnerTipoDocumento.getSelectedItem().toString();
                                        String numeroDocumento = txtNumeroDocumento.getText().toString();
                                        String usuario = txtUsuario.getText().toString();
                                        String detalle = txtDetalle.getText().toString();
                                        String micromovilidad = txtMicromovilidad.getText().toString();
                                        String imageUrl = imgView.getTag() != null ? imgView.getTag().toString() : "";

                                        // Obtener la fecha y hora actuales
                                        Calendar calendar = Calendar.getInstance();
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

                                        String fechaSalida = dateFormat.format(calendar.getTime());
                                        String horaSalida = timeFormat.format(calendar.getTime());

                                        Map<String, Object> datosSalida = new HashMap<>();
                                        datosSalida.put("tipoDocumento", tipoDocumento);
                                        datosSalida.put("numeroDocumento", numeroDocumento);
                                        datosSalida.put("usuario", usuario);
                                        datosSalida.put("detallePermiso", detalle);
                                        datosSalida.put("micromovilidad", micromovilidad);
                                        datosSalida.put("imageUrl", imageUrl);
                                        datosSalida.put("fechaSalida", fechaSalida);
                                        datosSalida.put("horaSalida", horaSalida);

                                        databaseReference.child("Permiso Salida").push().setValue(datosSalida)
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        showSuccessDialog(usuario, micromovilidad);
                                                    } else {
                                                        Toast.makeText(RegistroPermisoSalidaActivity.this, "Error al registrar salida", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(RegistroPermisoSalidaActivity.this, "Error al verificar código de usuario", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private void loadSpinnerData() {
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
                    int id = Integer.parseInt(snapshot.getKey());  // Obtén el ID (clave en Firebase)
                    String name = snapshot.getValue(String.class); // Obtén el nombre
                    items.add(name);                               // Añade el nombre al Spinner
                    idNameMap.put(id, name);                       // Guarda el par ID-Nombre en el mapa
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(RegistroPermisoSalidaActivity.this, android.R.layout.simple_spinner_item, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                // Guardar el mapa en una variable para usarlo luego
                if (spinner == spinnerTipoDocumento) {
                    tipoDocumentoMap = idNameMap;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RegistroPermisoSalidaActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void consultarNumDocumento(String numDocumento) {
        // Primero, intenta buscar en la tabla "estudiante"
        databaseReference.child("Permiso").orderByChild("numDocumento").equalTo(numDocumento)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Si encuentra el usuario en "estudiante", llena los datos
                            cargarDatosNumDocumento(dataSnapshot);
                        } else {
                            // Si no encuentra al usuario, muestra el mensaje
                            Toast.makeText(RegistroPermisoSalidaActivity.this, "Usuario no ingreso a Cibertec", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroPermisoSalidaActivity.this, "Error al consultar datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarDatosNumDocumento(DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            String tipoDocumento = getStringValue(snapshot, "tipoDocumento");
            String usuario = getStringValue(snapshot, "usuario");
            String detalle = getStringValue(snapshot, "detallePermiso");
            String imageUrl = getStringValue(snapshot, "imageUrl");
            String numDocumento = getStringValue(snapshot, "numDocumento");

            txtUsuario.setText(usuario);
            txtDetalle.setText(detalle);

            setSpinnerValue(spinnerTipoDocumento, tipoDocumento);

            setSpinnerValue(spinnerTipoDocumento, tipoDocumento);

            // Cargar la imagen con Glide
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(imgView);
                imgView.setTag(imageUrl);
            }

            // Llamar a la función para obtener micromovilidad desde Firebase
            if (numDocumento != null) {
                fetchMicromovilidad(numDocumento);
            }
        }
    }

    private String getStringValue(DataSnapshot snapshot, String key) {
        Object value = snapshot.child(key).getValue();
        return value != null ? value.toString() : null;
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

    // Función para obtener el dato de micromovilidad desde Firebase usando codigoUsuario
    private void fetchMicromovilidad(String numDocumento) {
        DatabaseReference permisoRef = databaseReference.child("Permiso");
        permisoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String numDocument = childSnapshot.child("numDocumento").getValue(String.class);

                    if (numDocument != null && numDocument.equals(numDocumento)) {
                        String numDocumento = childSnapshot.child("tipoMicromovilidad").getValue(String.class);
                        txtMicromovilidad.setText(numDocumento != null ? numDocumento : "");
                        found = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RegistroPermisoSalidaActivity.this, "Error al consultar micromovilidad", Toast.LENGTH_SHORT).show();
            }
        });
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

    //VALIDACIONES
    private boolean validateInputs() {
        if (txtNumeroDocumento.getText().toString().isEmpty()) {
            Toast.makeText(this, "Ingrese el N° Documento", Toast.LENGTH_SHORT).show();
            return false;
        }

        String numDocumento = txtNumeroDocumento.getText().toString();
        if (!numDocumento.isEmpty()) {
            if (numDocumento.length() != 8 || !numDocumento.matches("\\d+")) {
                Toast.makeText(this, "El número de documento debe tener 8 dígitos", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (txtUsuario.getText().toString().isEmpty()) {
            Toast.makeText(this, "Completar datos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (txtDetalle.getText().toString().isEmpty()) {
            Toast.makeText(this, "Ingrese el detalle de Permiso", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (txtMicromovilidad.getText().toString().isEmpty()) {
            Toast.makeText(this, "Usuario no ingreso a Cibertec", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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
        txtNumeroDocumento.setText("");
        txtUsuario.setText("");
        txtDetalle.setText("");
        txtMicromovilidad.setText("");
        spinnerTipoDocumento.setSelection(0);
        imgView.setImageResource(0);
    }
}