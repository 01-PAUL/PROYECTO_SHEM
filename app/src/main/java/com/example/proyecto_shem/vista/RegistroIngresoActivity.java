package com.example.proyecto_shem.vista;

import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class RegistroIngresoActivity extends AppCompatActivity {

    TextView txtCodigoUsuario, txtNumeroDocumento, txtUsuario, txtAutorizacion;
    Spinner spinnerTipoUsuario, spinnerTipoDocumento, spinnerMicromovilidad;
    Button btnEscanearQR, btnConsultar, btnIngreso, btnRegresar;
    ImageView imgView;

    private DatabaseReference databaseReference;
    private Map<Integer, String> tipoUsuarioMap;
    private Map<Integer, String> tipoDocumentoMap;

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

        // Referencia a Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Cargar los datos de Firebase en los spinners
        loadSpinnerData();

        // Inicialmente, deshabilita el botón Ingreso
        btnIngreso.setEnabled(false);
        btnIngreso.setBackgroundColor(0xFFA9A9A9);

        txtUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    btnIngreso.setEnabled(true);
                    btnIngreso.setBackgroundColor(Color.parseColor("#033657"));
                } else {
                    btnIngreso.setEnabled(false);
                    btnIngreso.setBackgroundColor(0xFFA9A9A9);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        txtAutorizacion.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateButtonState();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

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

        //Consultar por Codigo Usuario y Número de Documento
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()) {
                    String codUsuario = txtCodigoUsuario.getText().toString().replaceAll("\\s", "");
                    String numDocumento = txtNumeroDocumento.getText().toString().replaceAll("\\s", "");

                    // Actualizar los campos sin espacios en blanco
                    txtCodigoUsuario.setText(codUsuario);
                    txtNumeroDocumento.setText(numDocumento);

                    String codUsuarioPattern = "^[a-zA-Z]\\d{9}$";

                    // Validar si ambos campos están vacíos o contienen solo espacios
                    if (codUsuario.isEmpty() && numDocumento.isEmpty()) {
                        Toast.makeText(RegistroIngresoActivity.this, "Ingrese el Codigo Usuario o N° Documento", Toast.LENGTH_SHORT).show();
                    } else {
                        // Si el campo de codUsuario no está vacío, verificar su formato
                        if (!codUsuario.isEmpty()) {
                            if (codUsuario.matches(codUsuarioPattern)) {
                                consultarUsuario(codUsuario);
                            } else {
                                // Mostrar mensaje de error si el formato no es válido
                                Toast.makeText(RegistroIngresoActivity.this, "Formato de Código Usuario no válido", Toast.LENGTH_SHORT).show();
                                clearInputs(); // Limpiar todos los campos si el formato es incorrecto
                            }
                        } else if (!numDocumento.isEmpty()) {
                            consultarDocumento(numDocumento);
                        }
                    }
                }
            }
        });

        btnIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    String codigoUsuario = txtCodigoUsuario.getText().toString();

                    registrarIngreso(codigoUsuario);
                }
            }
        });



        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registrarIngreso(String codigoUsuario) {
        String tipoUsuario = spinnerTipoUsuario.getSelectedItem().toString();
        String tipoDocumento = spinnerTipoDocumento.getSelectedItem().toString();
        String numeroDocumento = txtNumeroDocumento.getText().toString();
        String usuario = txtUsuario.getText().toString();
        String tipoMicromovilidad = spinnerMicromovilidad.getSelectedItem().toString();
        String imageUrl = imgView.getTag() != null ? imgView.getTag().toString() : "";

        // Convertir codigoUsuario a mayúsculas
        codigoUsuario = codigoUsuario.toUpperCase();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String fechaIngreso = dateFormat.format(calendar.getTime());
        String horaIngreso = timeFormat.format(calendar.getTime());

        Map<String, Object> datosIngreso = new HashMap<>();
        datosIngreso.put("tipoUsuario", tipoUsuario);
        datosIngreso.put("codigoUsuario", codigoUsuario);
        datosIngreso.put("tipoDocumento", tipoDocumento);
        datosIngreso.put("numeroDocumento", numeroDocumento);
        datosIngreso.put("usuario", usuario);
        datosIngreso.put("tipoMicromovilidad", tipoMicromovilidad);
        datosIngreso.put("imageUrl", imageUrl);
        datosIngreso.put("fechaIngreso", fechaIngreso);
        datosIngreso.put("horaIngreso", horaIngreso);
        datosIngreso.put("estado", "activo"); // Añadir el campo de estado

        // Actualizar el estado en la tabla Salida a "inactivo" si el usuario ya existe
        databaseReference.child("Salida")
                .orderByChild("codigoUsuario")
                .equalTo(codigoUsuario)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().child("estado").setValue("inactivo"); // Cambiar estado a inactivo
                        }

                        // Registrar el ingreso después de actualizar el estado en Salida
                        databaseReference.child("Ingreso").push().setValue(datosIngreso)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        showSuccessDialog(usuario, tipoMicromovilidad);
                                    } else {
                                        Toast.makeText(RegistroIngresoActivity.this, "Error al registrar ingreso", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroIngresoActivity.this, "Error al verificar estado en Salida", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    // Método para cargar datos en los spinners desde Firebase
    private void loadSpinnerData() {
        loadSpinnerFromFirebase(databaseReference.child("tipoUsuario"), spinnerTipoUsuario);
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(RegistroIngresoActivity.this, android.R.layout.simple_spinner_item, items);
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
                Toast.makeText(RegistroIngresoActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //CODIGO QR
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Lectura cancelada...", Toast.LENGTH_LONG).show();
            } else {
                clearInputs();
                String qrContent = result.getContents();
                try {
                    JSONObject qrData = new JSONObject(qrContent);

                    // Obtiene el código de usuario desde el QR
                    String codigoUsuario = qrData.getString("codigoUsuario");

                    // Asigna el código al campo de texto
                    txtCodigoUsuario.setText(codigoUsuario);

                    // Llama al método para consultar datos en Firebase usando el código escaneado
                    consultarUsuario(codigoUsuario);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Código de Usuario no válido", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Para el Consultar por CODIGO USUARIO
    private void consultarUsuario(String codUsuario) {
        String codUsuarioUpper = codUsuario.toUpperCase();

        // Verificar si el usuario ya existe en la tabla Ingreso y está "activo"
        databaseReference.child("Ingreso")
                .orderByChild("codigoUsuario")
                .equalTo(codUsuarioUpper)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean usuarioActivoEncontrado = false;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String estado = snapshot.child("estado").getValue(String.class);

                            if ("activo".equals(estado)) {
                                usuarioActivoEncontrado = true;
                                break;
                            }
                        }

                        if (usuarioActivoEncontrado) {
                            // Usuario ya tiene un registro activo en "Ingreso"
                            Toast.makeText(RegistroIngresoActivity.this, "Usuario ya ingresó a Cibertec", Toast.LENGTH_SHORT).show();
                            clearInputs();
                        } else {
                            // No se encontró un registro activo, el usuario puede ingresar de nuevo
                            consultarCodUsuario(codUsuarioUpper);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroIngresoActivity.this, "Error al verificar el ingreso", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void consultarCodUsuario(String codUsuario) {
        String codUsuarioUpper = codUsuario.toUpperCase();
        databaseReference.child("estudiante").orderByChild("codUsuario").equalTo(codUsuarioUpper)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            cargarDatosCodUsuario(dataSnapshot); // Si encuentra el usuario, carga los datos
                        } else {
                            // Si no se encuentra en "estudiante", busca en "docente"
                            buscarEnDocente(codUsuario);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroIngresoActivity.this, "Error al consultar datos", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void buscarEnDocente(String codUsuario) {
        databaseReference.child("docente").orderByChild("codUsuario").equalTo(codUsuario)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Si encuentra el usuario en "docente", llena los datos
                            cargarDatosCodUsuario(dataSnapshot);
                        } else {
                            // Si no encuentra en "docente", busca en "personalAdministrativo"
                            buscarEnPersonalAdministrativo(codUsuario);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroIngresoActivity.this, "Error al consultar datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void buscarEnPersonalAdministrativo(String codUsuario) {
        databaseReference.child("personalAdministrativo").orderByChild("codUsuario").equalTo(codUsuario)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Si encuentra el usuario en "personalAdministrativo", llena los datos
                            cargarDatosCodUsuario(dataSnapshot);
                        } else {
                            // Si no encuentra en ninguna tabla
                            Toast.makeText(RegistroIngresoActivity.this, "Código de Usuario no válido", Toast.LENGTH_SHORT).show();
                            clearInputs();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroIngresoActivity.this, "Error al consultar datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para el CONSULTAR donde cargue los datos del usuario una vez encontrado
    private void cargarDatosCodUsuario(DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            int idTipoUsuario = Integer.parseInt(getStringValue(snapshot, "idTipoUsuario"));
            int idTipoDocumento = Integer.parseInt(getStringValue(snapshot, "idTipoDocumento"));
            String numeroDocumento = getStringValue(snapshot, "numDocumento");
            String nombres = getStringValue(snapshot, "nombres");
            String apellidos = getStringValue(snapshot, "apellidos");
            String autorizacion = getStringValue(snapshot, "autorizacion");
            String imageUrl = getStringValue(snapshot, "imageUrl");

            // Combina los nombres y apellidos
            String usuario = (nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "");

            txtUsuario.setText(usuario);
            txtAutorizacion.setText(autorizacion != null ? autorizacion : "No Autorizado");
            txtNumeroDocumento.setText(numeroDocumento != null ? numeroDocumento : "");

            // Usar el mapa para obtener el nombre según el ID y luego setear el valor en el Spinner
            String tipoUsuario = tipoUsuarioMap.get(idTipoUsuario);
            String tipoDocumento = tipoDocumentoMap.get(idTipoDocumento);

            setSpinnerValue(spinnerTipoUsuario, tipoUsuario);
            setSpinnerValue(spinnerTipoDocumento, tipoDocumento);

            updateButtonState(); // Actualizar el estado del botón de ingreso según la autorización

            // Cargar la imagen usando Glide
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(imgView);
                imgView.setTag(imageUrl);
            }
        }
    }

    //Para el Consultar por NÚMERO DE DOCUMENTO
    private void consultarDocumento(String numDocumento) {
        String numDocumentoUpper = numDocumento.toUpperCase();

        // Verificar si el usuario ya existe en la tabla Ingreso y está "activo"
        databaseReference.child("Ingreso")
                .orderByChild("numeroDocumento")
                .equalTo(numDocumentoUpper)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean usuarioActivoEncontrado = false;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String estado = snapshot.child("estado").getValue(String.class);

                            if ("activo".equals(estado)) {
                                usuarioActivoEncontrado = true;
                                break;
                            }
                        }

                        if (usuarioActivoEncontrado) {
                            // Usuario ya tiene un registro activo en "Ingreso"
                            Toast.makeText(RegistroIngresoActivity.this, "Usuario ya ingresó a Cibertec", Toast.LENGTH_SHORT).show();
                            clearInputs();
                        } else {
                            // No se encontró un registro activo, el usuario puede ingresar de nuevo
                            consultarNumDocumento(numDocumentoUpper);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroIngresoActivity.this, "Error al verificar el ingreso", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void consultarNumDocumento(String numDocumento) {
        // Primero, intenta buscar en la tabla "estudiante"
        databaseReference.child("estudiante").orderByChild("numDocumento").equalTo(numDocumento)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Si encuentra el usuario en "estudiante", llena los datos
                            cargarDatosNumDocumento(dataSnapshot);
                        } else {
                            // Si no encuentra en "estudiante", busca en "docente"
                            buscarEnDocenteNumDoc(numDocumento);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroIngresoActivity.this, "Error al consultar datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void buscarEnDocenteNumDoc(String numDocumento) {
        databaseReference.child("docente").orderByChild("numDocumento").equalTo(numDocumento)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Si encuentra el usuario en "docente", llena los datos
                            cargarDatosNumDocumento(dataSnapshot);
                        } else {
                            // Si no encuentra en "docente", busca en "personalAdministrativo"
                            buscarEnPersonalAdmin(numDocumento);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroIngresoActivity.this, "Error al consultar datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void buscarEnPersonalAdmin(String numDocumento) {
        databaseReference.child("personalAdministrativo").orderByChild("numDocumento").equalTo(numDocumento)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Si encuentra el usuario en "personalAdministrativo", llena los datos
                    cargarDatosNumDocumento(dataSnapshot);
                } else {
                    // Si no encuentra en ninguna tabla
                    Toast.makeText(RegistroIngresoActivity.this, "Número de Documento no válido", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RegistroIngresoActivity.this, "Error al consultar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para el CONSULTAR donde cargue los datos del usuario una vez encontrado
    private void cargarDatosNumDocumento(DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            int idTipoUsuario = Integer.parseInt(getStringValue(snapshot, "idTipoUsuario"));
            int idTipoDocumento = Integer.parseInt(getStringValue(snapshot, "idTipoDocumento"));
            String nombres = getStringValue(snapshot, "nombres");
            String apellidos = getStringValue(snapshot, "apellidos");
            String autorizacion = getStringValue(snapshot, "autorizacion");
            String imageUrl = getStringValue(snapshot, "imageUrl");
            String codigoUsuario = getStringValue(snapshot, "codUsuario"); // Obtener el código de usuario

            // Combina los nombres y apellidos
            String usuario = (nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "");

            txtUsuario.setText(usuario);
            txtAutorizacion.setText(autorizacion != null ? autorizacion : "No Autorizado");
            txtCodigoUsuario.setText(codigoUsuario != null ? codigoUsuario : "");

            // Usar el mapa para obtener el nombre según el ID y luego setear el valor en el Spinner
            String tipoUsuario = tipoUsuarioMap.get(idTipoUsuario);
            String tipoDocumento = tipoDocumentoMap.get(idTipoDocumento);

            setSpinnerValue(spinnerTipoUsuario, tipoUsuario);
            setSpinnerValue(spinnerTipoDocumento, tipoDocumento);

            updateButtonState(); // Actualizar el estado del botón de ingreso según la autorización

            // Cargar la imagen usando Glide
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(imgView);
                imgView.setTag(imageUrl); // Guardar la URL en el tag del ImageView
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

    //Cuando la Autorizacion es No Autorizado se pone en otro color
    //Cuando la Autorizacion es Autorizado vuelvea su color normal
    private void updateButtonState() {
        String authorizationText = txtAutorizacion.getText().toString();
        if (authorizationText.equalsIgnoreCase("No Autorizado")) {
            btnIngreso.setEnabled(false);
            btnIngreso.setBackgroundColor(0xFFA9A9A9);
        } else if (authorizationText.equalsIgnoreCase("Autorizado")) {
            btnIngreso.setEnabled(true);
            btnIngreso.setBackgroundColor(Color.parseColor("#033657"));
        }
    }

    //VALIDACIONES
    private boolean validateInputs() {

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

    //LIMPIAR LOS CAMPOS LUEGO DE REGISTRAR EL INGRESO
    private void clearInputs() {
        txtCodigoUsuario.setText("");
        txtNumeroDocumento.setText("");
        txtUsuario.setText("");
        txtAutorizacion.setText("");
        spinnerTipoUsuario.setSelection(0);
        spinnerTipoDocumento.setSelection(0);
        spinnerMicromovilidad.setSelection(0);
        imgView.setImageResource(R.drawable.ic_launcher_foreground);
    }

}
