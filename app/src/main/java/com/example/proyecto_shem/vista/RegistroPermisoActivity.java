package com.example.proyecto_shem.vista;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistroPermisoActivity extends AppCompatActivity {

    TextView txtNumeroDocumento, txtUsuario, txtGenero, txtFechNacim, txtDetalle;
    Spinner spinnerTipoDocumento, spinnerMicromovilidad;
    Button btnConsultar, btnRegistrar, btnRegresar;
    ImageView imgView;

    private DatabaseReference databaseReference;
    private Map<Integer, String> tipoDocumentoMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_permiso);

        // Inicializar componentes de la vista
        txtNumeroDocumento = findViewById(R.id.txtNumeroDocumento);
        txtUsuario = findViewById(R.id.txtUsuario);
        txtGenero = findViewById(R.id.txtGenero);
        txtFechNacim = findViewById(R.id.txtFechNacim);
        txtDetalle = findViewById(R.id.txtDetalle);
        spinnerTipoDocumento = findViewById(R.id.spinnerTipoDocumento);
        spinnerMicromovilidad = findViewById(R.id.spinnerMicromovilidad);
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
                    Toast.makeText(RegistroPermisoActivity.this, "Ingrese el N° Documento", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    String numDocumento = txtNumeroDocumento.getText().toString().toLowerCase();

                    // Verificar si el usuario ya existe en la tabla Ingreso
                    databaseReference.child("Permiso Ingreso")
                            .orderByChild("numDocumento")
                            .equalTo(numDocumento)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // El usuario ya ingresó, mostrar mensaje
                                        Toast.makeText(RegistroPermisoActivity.this, "Usuario ya ingresó a Cibertec", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // El usuario no existe, registrar datos
                                        registrarPermiso(numDocumento); // Pasar el código en minúsculas
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(RegistroPermisoActivity.this, "Error al verificar el permiso", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private void registrarPermiso(String numDocumento) {
        String tipoDocumento = spinnerTipoDocumento.getSelectedItem().toString();
        String usuario = txtUsuario.getText().toString();
        String genero = txtGenero.getText().toString();
        String fechNacim = txtFechNacim.getText().toString();
        String detalle = txtDetalle.getText().toString();
        String tipoMicromovilidad = spinnerMicromovilidad.getSelectedItem().toString();
        String imageUrl = imgView.getTag() != null ? imgView.getTag().toString() : "";

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String fechaIngreso = dateFormat.format(calendar.getTime());
        String horaIngreso = timeFormat.format(calendar.getTime());

        Map<String, Object> datosPermiso = new HashMap<>();
        datosPermiso.put("tipoDocumento", tipoDocumento);
        datosPermiso.put("numDocumento", numDocumento);
        datosPermiso.put("usuario", usuario);
        datosPermiso.put("genero", genero);
        datosPermiso.put("fechNacim", fechNacim);
        datosPermiso.put("detallePermiso", detalle);
        datosPermiso.put("tipoMicromovilidad", tipoMicromovilidad);
        datosPermiso.put("imageUrl", imageUrl);
        datosPermiso.put("fechaIngreso", fechaIngreso);
        datosPermiso.put("horaIngreso", horaIngreso);

        databaseReference.child("Permiso Ingreso").push().setValue(datosPermiso)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showSuccessDialog(usuario, tipoMicromovilidad);
                    } else {
                        Toast.makeText(RegistroPermisoActivity.this, "Error al registrar permiso", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadSpinnerData() {
        loadSpinnerFromFirebase(databaseReference.child("tipoDocumento"), spinnerTipoDocumento);
        loadSpinnerFromFirebase(databaseReference.child("tipoMicromovilidad"), spinnerMicromovilidad);
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(RegistroPermisoActivity.this, android.R.layout.simple_spinner_item, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                // Guardar el mapa en una variable para usarlo luego
                if (spinner == spinnerTipoDocumento) {
                    tipoDocumentoMap = idNameMap;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RegistroPermisoActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void consultarNumDocumento(String numDocumento) {
        // Primero, intenta buscar en la tabla "estudiante"
        databaseReference.child("dni").orderByChild("numDocumento").equalTo(numDocumento)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Si encuentra el usuario en "estudiante", llena los datos
                    cargarDatosNumDocumento(dataSnapshot);
                } else {
                    // Si no encuentra en "estudiante", busca en "docente"
                    buscarEnCarnetExt(numDocumento);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RegistroPermisoActivity.this, "Error al consultar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarEnCarnetExt(String numDocumento) {
        databaseReference.child("carnet_ext").orderByChild("numDocumento").equalTo(numDocumento)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Si encuentra el usuario en "docente", llena los datos
                    cargarDatosNumDocumento(dataSnapshot);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RegistroPermisoActivity.this, "Error al consultar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para el CONSULTAR donde cargue los datos del usuario una vez encontrado
    private void cargarDatosNumDocumento(DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            int idTipoDocumento = Integer.parseInt(getStringValue(snapshot, "idTipoDocumento"));
            String nombres = getStringValue(snapshot, "nombres");
            String apellidos = getStringValue(snapshot, "apellidos");
            String genero = getStringValue(snapshot, "genero");
            String fechaNacimiento = getStringValue(snapshot, "fechaNacimiento");
            String imageUrl = getStringValue(snapshot, "imgUsuarioExt");
            String imageUrlReniec = getStringValue(snapshot, "imgUsuarioReniec");

            // Combinar nombres y apellidos
            String usuario = (nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "");

            txtUsuario.setText(usuario);
            txtGenero.setText(genero);
            txtFechNacim.setText(fechaNacimiento);

            String tipoDocumento = tipoDocumentoMap.get(idTipoDocumento);

            setSpinnerValue(spinnerTipoDocumento, tipoDocumento);

            // Cargar la imagen con Glide
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(imgView);
                imgView.setTag(imageUrl);
            } else if (imageUrlReniec != null && !imageUrlReniec.isEmpty()) {
                Glide.with(this)
                        .load(imageUrlReniec)
                        .into(imgView);
                imgView.setTag(imageUrlReniec);
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

    //MENSAJE REGISTRADO
    private void showSuccessDialog(String usuario, String tipoMicromovilidad) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("INGRESO REGISTRADO DE FORMA EXITOSA!!!!");
        builder.setMessage("INGRESO A CIBERTEC\n" + usuario + "\n" +
                        "TIPO DE MICROMOVILIDAD: " + tipoMicromovilidad)
                .setPositiveButton("Aceptar", (dialog, id) -> dialog.dismiss());


        AlertDialog dialog = builder.create();
        dialog.show();
        clearInputs();
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

        if (txtDetalle.getText().toString().isEmpty()) {
            Toast.makeText(this, "Ingrese el detalle de Permiso", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spinnerMicromovilidad.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione un tipo de micromovilidad", Toast.LENGTH_SHORT).show();
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

    private void clearInputs() {
        txtNumeroDocumento.setText("");
        txtUsuario.setText("");
        txtGenero.setText("");
        txtFechNacim.setText("");
        txtDetalle.setText("");
        spinnerTipoDocumento.setSelection(0);
        spinnerMicromovilidad.setSelection(0);
        imgView.setImageResource(0);
    }
}
