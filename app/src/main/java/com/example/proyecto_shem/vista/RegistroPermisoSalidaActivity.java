package com.example.proyecto_shem.vista;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

        TextView txtNumeroDocumento, txtUsuario, txtArea, txtDetalle, txtMicromovilidad;
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
        txtArea = findViewById(R.id.txtArea);
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

        // Inicialmente, deshabilita el botón Registrar
        btnRegistrar.setEnabled(false);
        btnRegistrar.setBackgroundColor(0xFFA9A9A9);

        txtUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Habilita el botón Registrar solo si txtUsuario tiene texto y cambia su color
                if (s.length() > 0) {
                    btnRegistrar.setEnabled(true);
                    btnRegistrar.setBackgroundColor(Color.parseColor("#033657"));
                } else {
                    btnRegistrar.setEnabled(false);
                    btnRegistrar.setBackgroundColor(0xFFA9A9A9); // Color gris
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Configuración de botones
        btnRegresar.setOnClickListener(v -> finish());

        btnConsultar.setOnClickListener(v -> {
            if (validarCampos()) {
                String numDocumento = txtNumeroDocumento.getText().toString().trim();
                if (numDocumento.isEmpty()) {
                    Toast.makeText(RegistroPermisoSalidaActivity.this, "Ingrese el N° Documento", Toast.LENGTH_SHORT).show();
                } else {
                    // Si uno de los campos tiene un valor, procede con la consulta
                    if (!numDocumento.isEmpty()) {
                        consultarDocumento(numDocumento);
                    }
                }
            }
        });

        // Acción al presionar el botón Salida
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    String numDocumento = txtNumeroDocumento.getText().toString();

                    // Proceder directamente con el registro de salida sin verificar en "Permiso Salida"
                    String tipoDocumento = spinnerTipoDocumento.getSelectedItem().toString();
                    String numeroDocumento = txtNumeroDocumento.getText().toString();
                    String usuario = txtUsuario.getText().toString();
                    String area = txtArea.getText().toString();
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
                    datosSalida.put("area", area);
                    datosSalida.put("detallePermiso", detalle);
                    datosSalida.put("micromovilidad", micromovilidad);
                    datosSalida.put("imageUrl", imageUrl);
                    datosSalida.put("fechaSalida", fechaSalida);
                    datosSalida.put("horaSalida", horaSalida);
                    datosSalida.put("estado", "activo"); // Añadir el campo de estado

                    databaseReference.child("Permiso Salida").push().setValue(datosSalida)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    showSuccessDialog(usuario, micromovilidad);

                                    // Actualizar estado en la tabla Permiso a "inactivo"
                                    databaseReference.child("Permiso Ingreso")
                                            .orderByChild("numeroDocumento")
                                            .equalTo(numDocumento)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        snapshot.getRef().child("estado").setValue("inactivo"); // Cambiar estado a inactivo
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    Toast.makeText(RegistroPermisoSalidaActivity.this, "Error al actualizar estado en Ingreso", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(RegistroPermisoSalidaActivity.this, "Error al registrar salida", Toast.LENGTH_SHORT).show();
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

    private void consultarDocumento(String numDocumento) {
        String numDocumentoUpper = numDocumento.toUpperCase();

        // Verificar si el usuario ya existe en la tabla Ingreso
        databaseReference.child("Permiso Salida")
                .orderByChild("numeroDocumento")
                .equalTo(numDocumentoUpper)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String estado = snapshot.child("estado").getValue(String.class);

                                if ("inactivo".equals(estado)) {
                                    // El usuario puede volver a ingresar
                                    consultarNumDocumento(numDocumentoUpper);
                                } else {
                                    // El usuario ya salió, mostrar mensaje
                                    Toast.makeText(RegistroPermisoSalidaActivity.this, "Usuario ya salió de Cibertec", Toast.LENGTH_SHORT).show();
                                    clearInputs();
                                }
                            }
                        } else {
                            // Si el usuario no está en Ingreso, consultar en las otras tablas
                            consultarNumDocumento(numDocumentoUpper);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroPermisoSalidaActivity.this, "Error al verificar el ingreso", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void consultarNumDocumento(String numDocumento) {
        // Primero, intenta buscar en la tabla "Permiso Ingreso"
        databaseReference.child("Permiso Ingreso").orderByChild("numeroDocumento").equalTo(numDocumento)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       boolean usuarioActivoEncontrado = false;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String estado = snapshot.child("estado").getValue(String.class);

                            if ("activo".equals(estado)) {
                                usuarioActivoEncontrado = true;
                                // Usuario está activo, cargar datos del usuario
                                cargarDatosNumDocumento(dataSnapshot);
                                break; // Deja de buscar cuando encuentras un usuario activo
                            }
                        }

                        if (!usuarioActivoEncontrado) {
                            // No hay usuarios activos encontrados, mostrar mensaje
                            Toast.makeText(RegistroPermisoSalidaActivity.this, "Usuario no ingreso a Cibertec", Toast.LENGTH_SHORT).show();
                            clearInputs();
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
            String area = getStringValue(snapshot, "area");
            String detalle = getStringValue(snapshot, "detallePermiso");
            String imageUrl = getStringValue(snapshot, "imageUrl");
            String numDocumento = getStringValue(snapshot, "numeroDocumento");

            txtUsuario.setText(usuario);
            txtArea.setText(area);
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
        DatabaseReference permisoRef = databaseReference.child("Permiso Ingreso");
        permisoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String numDocument = childSnapshot.child("numeroDocumento").getValue(String.class);
                    String estado = childSnapshot.child("estado").getValue(String.class);

                    if (numDocument != null && numDocument.equals(numDocumento) && "activo".equals(estado)) {
                        // Solo establece el valor de micromovilidad si el estado es activo
                        String micromovilidad = childSnapshot.child("tipoMicromovilidad").getValue(String.class);
                        txtMicromovilidad.setText(micromovilidad != null ? micromovilidad : "");
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // Si no se encontró un usuario activo, establece un valor predeterminado o deja el campo vacío
                    txtMicromovilidad.setText("");
                    Toast.makeText(RegistroPermisoSalidaActivity.this, "Micromovilidad no encontrada para usuario activo", Toast.LENGTH_SHORT).show();
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

        if (txtDetalle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingrese el detalle de Permiso", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (txtMicromovilidad.getText().toString().isEmpty()) {
            Toast.makeText(this, "Usuario no ingreso a Cibertec", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (txtArea.getText().toString().isEmpty()) {
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
        txtArea.setText("");
        txtDetalle.setText("");
        txtMicromovilidad.setText("");
        spinnerTipoDocumento.setSelection(0);
        imgView.setImageResource(R.drawable.ic_launcher_foreground);
    }
}