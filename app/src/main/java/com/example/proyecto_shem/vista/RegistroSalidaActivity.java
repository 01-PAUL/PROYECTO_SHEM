package com.example.proyecto_shem.vista;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    TextView txtUsuario, txtMovilidad;
    Spinner spinnerTipoUsuario, spinnerTipoDocumento;
    Button btnEscanearQR, btnConsultar, btnSalida, btnRegresar;
    ImageView imgView;
    EditText txtNumeroDocumento, txtCodigoUsuario;

    private DatabaseReference databaseReference;
    private Map<Integer, String> tipoUsuarioMap;
    private Map<Integer, String> tipoDocumentoMap;
    boolean isInternalChange = false;
    private boolean isScanningQR = false;

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

        // Inicialmente, deshabilita el botón Salida
        btnSalida.setEnabled(false);
        btnSalida.setBackgroundColor(0xFFA9A9A9);

        txtUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    btnSalida.setEnabled(true);
                    btnSalida.setBackgroundColor(Color.parseColor("#033657"));
                } else {
                    btnSalida.setEnabled(false);
                    btnSalida.setBackgroundColor(0xFFA9A9A9);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        txtNumeroDocumento.addTextChangedListener(new TextWatcher() {
            private int previousLength = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No es necesario implementar
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();

                // Verificar si hay caracteres no numéricos
                if (!input.matches("\\d*")) {
                    txtNumeroDocumento.removeTextChangedListener(this); // Evitar bucles
                    // Eliminar caracteres no numéricos
                    txtNumeroDocumento.setText(input.replaceAll("[^\\d]", ""));
                    txtNumeroDocumento.setSelection(txtNumeroDocumento.getText().length());
                    txtNumeroDocumento.addTextChangedListener(this);
                }

                // Validar si se borró un dígito
                if (input.length() < previousLength) {
                    clearDocumento();  // Limpiar los campos si se borró un dígito
                }

                // Actualizar la longitud previa después de que se haya modificado el texto
                previousLength = input.length();

                // Aquí podrías agregar lógica para realizar la consulta de los datos si el número de documento es válido
            }
        });

        spinnerTipoDocumento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isScanningQR) {
                    isScanningQR = false; // Desactiva la bandera
                    return; // No realizar validaciones
                }

                String tipoDocumento = spinnerTipoDocumento.getSelectedItem().toString();
                String numDocumento = txtNumeroDocumento.getText().toString();

                if (tipoDocumento.equals("DNI")) {
                    // Limitar la longitud a 8 caracteres
                    txtNumeroDocumento.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                    // Si el número actual tiene más de 8 caracteres, recortar
                    if (numDocumento.length() > 8) {
                        txtNumeroDocumento.setText(numDocumento.substring(0, 8));
                        txtNumeroDocumento.setSelection(8); // Coloca el cursor al final
                    }
                } else if (tipoDocumento.equals("Carnet Ext.")) {
                    // Limitar la longitud a 11 caracteres
                    txtNumeroDocumento.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                } else {
                    // Si no se selecciona un tipo, dejar la longitud máxima en 11
                    txtNumeroDocumento.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se seleccionó nada, permitir por defecto hasta 11 caracteres
                txtNumeroDocumento.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
            }
        });

        txtCodigoUsuario.addTextChangedListener(new TextWatcher() {

            private boolean isInternalChange = false; // Control para evitar bucles infinitos
            private int previousLengthCodigoUsuario = 0; // Longitud previa del texto en txtCodigoUsuario

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Almacenar la longitud previa antes del cambio
                previousLengthCodigoUsuario = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No es necesario implementar
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isInternalChange) return; // Ignorar cambios internos
                isInternalChange = true;

                String input = s.toString();

                // Verificar si hay espacios iniciales y eliminarlos
                if (!input.isEmpty() && input.charAt(0) == ' ') {
                    txtCodigoUsuario.setText(input.trim());
                    txtCodigoUsuario.setSelection(txtCodigoUsuario.getText().length());
                } else {
                    // Limitar la longitud máxima
                    txtCodigoUsuario.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                }

                // Verificar si se borró un dígito (la longitud actual es menor que la previa)
                if (input.length() < previousLengthCodigoUsuario) {
                    clearCodigo(); // Limpiar los campos excepto txtNumeroDocumento
                }

                // Actualizar la longitud previa
                previousLengthCodigoUsuario = input.length();

                isInternalChange = false;
            }
        });

        spinnerTipoUsuario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isScanningQR) {
                    isScanningQR = false; // Desactiva la bandera
                    return; // No realizar validaciones
                }

                String tipoUsuario = spinnerTipoUsuario.getSelectedItem().toString();

                if (tipoUsuario.equals("Seleccione:")) {
                    // Restablecer el campo si se selecciona "Seleccione"
                    txtCodigoUsuario.setText("");
                    txtCodigoUsuario.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                    return; // Salir para evitar operaciones adicionales
                }

                String codigoUsuarioActual = txtCodigoUsuario.getText().toString();
                if (!codigoUsuarioActual.isEmpty()) {
                    // Obtener la letra inicial correcta según el tipo
                    char letraInicial = getLetraInicial(tipoUsuario,
                            Character.isLowerCase(codigoUsuarioActual.charAt(0)));
                    // Actualizar el texto del código usuario
                    String nuevoCodigo = letraInicial + codigoUsuarioActual.substring(1);
                    txtCodigoUsuario.setText(nuevoCodigo);
                    txtCodigoUsuario.setSelection(nuevoCodigo.length()); // Mover el cursor al final
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Comportamiento por defecto si nada está seleccionado
                txtCodigoUsuario.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
            }
        });

        // Acción al presionar el botón EscanearQR
        btnEscanearQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isScanningQR = true; // Indicar que se está escaneando un QR
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
                    String codUsuario = txtCodigoUsuario.getText().toString().replaceAll("\\s", "");
                    String numDocumento = txtNumeroDocumento.getText().toString().replaceAll("\\s", "");

                    // Actualizar los campos sin espacios en blanco
                    txtCodigoUsuario.setText(codUsuario);
                    txtNumeroDocumento.setText(numDocumento);

                    // Validación para el Código Usuario
                    if (codUsuario.isEmpty() && numDocumento.isEmpty()) {
                        Toast.makeText(RegistroSalidaActivity.this, "Ingrese el Código Usuario o N° Documento", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!codUsuario.isEmpty()) {
                            // Primero, verificar que el código tiene una letra seguida de 9 números
                            if (!codUsuario.matches("^[a-zA-Z]\\d{9}$")) {
                                Toast.makeText(RegistroSalidaActivity.this, "Formato de Código debe tener una letra y 9 números", Toast.LENGTH_SHORT).show();
                            } else {
                                // Luego, verificar que la letra inicial sea 'I', 'P' o 'A'
                                if (!codUsuario.matches("^[IiPpAa].*")) {
                                    Toast.makeText(RegistroSalidaActivity.this, "Formato solo se permiten I, P, A como letra inicial", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Si todo es válido, llamar a la función para consultar el usuario
                                    consultarUsuario(codUsuario);
                                }
                            }
                        } else if (!numDocumento.isEmpty() && numDocumento.matches("\\d+")) {
                            consultarDocumento(numDocumento);
                        }
                    }
                }
            }
        });


        // Acción al presionar el botón Salida
        btnSalida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    String codigoUsuario = txtCodigoUsuario.getText().toString().toUpperCase();

                    // Verificar si el usuario está activo en la tabla "Ingreso"
                    databaseReference.child("Ingreso")
                            .orderByChild("codigoUsuario")
                            .equalTo(codigoUsuario)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    boolean usuarioActivoEncontrado = false;

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        String estado = snapshot.child("estado").getValue(String.class);

                                        if ("activo".equals(estado)) {
                                            usuarioActivoEncontrado = true;
                                            // Usuario está activo, proceder con el registro de salida
                                            registrarSalida(snapshot);
                                            break; // Detener búsqueda al encontrar usuario activo
                                        }
                                    }

                                    if (!usuarioActivoEncontrado) {
                                        // Si no se encuentra activo, mostrar mensaje
                                        Toast.makeText(RegistroSalidaActivity.this, "Usuario ya salió de Cibertec", Toast.LENGTH_SHORT).show();
                                        clearInputs();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(RegistroSalidaActivity.this, "Error al verificar el ingreso", Toast.LENGTH_SHORT).show();
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

    private String getCodigoUsuarioRegex(String tipoUsuario) {
        switch (tipoUsuario) {
            case "Estudiante":
                return "^[Ii]\\d{0,9}$"; // Una 'I' o 'i' seguida de hasta 9 dígitos
            case "Docente":
                return "^[Pp]\\d{0,9}$"; // Una 'P' o 'p' seguida de hasta 9 dígitos
            case "Personal Administrativo":
                return "^[Aa]\\d{0,9}$"; // Una 'A' o 'a' seguida de hasta 9 dígitos
            default:
                return "^[IiPpAa]\\d{0,9}$"; // 'I', 'P', o 'A' seguida de hasta 9 dígitos
        }
    }

    private char getLetraInicial(String tipoUsuario, boolean esMinuscula) {
        char letra = ' ';
        switch (tipoUsuario) {
            case "Estudiante":
                letra = 'I';
                break;
            case "Docente":
                letra = 'P';
                break;
            case "Personal Administrativo":
                letra = 'A';
                break;
            default:
                letra = ' '; // O dejar en blanco si no se selecciona un tipo
        }

        // Convertir a minúscula si el campo actual está en minúscula
        return esMinuscula ? Character.toLowerCase(letra) : letra;
    }

    private void registrarSalida(DataSnapshot snapshotIngreso) {
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
        datosSalida.put("codigoUsuario", snapshotIngreso.child("codigoUsuario").getValue(String.class));
        datosSalida.put("tipoDocumento", tipoDocumento);
        datosSalida.put("numeroDocumento", numeroDocumento);
        datosSalida.put("usuario", usuario);
        datosSalida.put("micromovilidad", micromovilidad);
        datosSalida.put("imageUrl", imageUrl);
        datosSalida.put("fechaSalida", fechaSalida);
        datosSalida.put("horaSalida", horaSalida);
        datosSalida.put("estado", "activo"); // Añadir el campo de estado

        databaseReference.child("Salida").push().setValue(datosSalida)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showSuccessDialog(usuario, micromovilidad);

                        // Actualizar estado en la tabla "Ingreso" a "inactivo"
                        snapshotIngreso.getRef().child("estado").setValue("inactivo")
                                .addOnCompleteListener(updateTask -> {
                                    if (!updateTask.isSuccessful()) {
                                        Toast.makeText(RegistroSalidaActivity.this, "Error al actualizar estado en Ingreso", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(RegistroSalidaActivity.this, "Error al registrar salida", Toast.LENGTH_SHORT).show();
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
                clearInputs();
                String qrContent = result.getContents();
                try {
                    JSONObject qrData = new JSONObject(qrContent);

                    // Extraer los campos del JSON, incluyendo codigoUsuario
                    String codigoUsuario = qrData.getString("codigoUsuario");

                    // Asignar a los TextView y Spinners
                    txtCodigoUsuario.setText(codigoUsuario);

                    consultarSalida(codigoUsuario);

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
    private void consultarSalida(String codUsuario) {
        String codUsuarioUpper = codUsuario.toUpperCase();

        // Verificar si el usuario ya existe en la tabla Ingreso
        databaseReference.child("Salida")
                .orderByChild("codigoUsuario")
                .equalTo(codUsuarioUpper)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String estado = snapshot.child("estado").getValue(String.class);

                                if ("inactivo".equals(estado)) {
                                    // El usuario puede volver a ingresar
                                    consultarUsuario(codUsuarioUpper);
                                } else {
                                    // El usuario ya salió, mostrar mensaje
                                    Toast.makeText(RegistroSalidaActivity.this, "Usuario ya salió de Cibertec", Toast.LENGTH_SHORT).show();
                                    clearInputs();
                                }
                            }
                        } else {
                            // Si el usuario no está en Salida, consultar en las otras tablas
                            consultarUsuario(codUsuarioUpper);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroSalidaActivity.this, "Error al verificar el ingreso", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void consultarUsuario(String codUsuario) {
        String codUsuarioUpper = codUsuario.toUpperCase();

        // Verificar si el código existe en alguna de las tablas de Firebase
        databaseReference.child("estudiante").orderByChild("codUsuario").equalTo(codUsuarioUpper)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            // No existe en "estudiante", verificar en "docente"
                            verificarEnDocente(codUsuarioUpper);
                        } else {
                            // Código encontrado en "estudiante", verificar en "Ingreso"
                            verificarEnIngreso(dataSnapshot, codUsuarioUpper);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroSalidaActivity.this, "Error al consultar el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verificarEnDocente(String codUsuario) {
        databaseReference.child("docente").orderByChild("codUsuario").equalTo(codUsuario)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            // No existe en "docente", verificar en "personalAdministrativo"
                            verificarEnPersonalAdministrativo(codUsuario);
                        } else {
                            // Código encontrado en "docente", verificar en "Ingreso"
                            verificarEnIngreso(dataSnapshot, codUsuario);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroSalidaActivity.this, "Error al consultar el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verificarEnPersonalAdministrativo(String codUsuario) {
        databaseReference.child("personalAdministrativo").orderByChild("codUsuario").equalTo(codUsuario)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            clearInputs(); // Limpiar todos los campos si el usuario no existe
                            // El código no se encuentra en ninguna de las tablas, mostrar mensaje
                            Toast.makeText(RegistroSalidaActivity.this, "Código de Usuario no válido", Toast.LENGTH_SHORT).show();
                        } else {
                            // Código encontrado en "personalAdministrativo", verificar en "Ingreso"
                            verificarEnIngreso(dataSnapshot, codUsuario);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroSalidaActivity.this, "Error al consultar el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verificarEnIngreso(DataSnapshot dataSnapshotUsuario, String codUsuario) {
        databaseReference.child("Ingreso").orderByChild("codigoUsuario").equalTo(codUsuario)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotIngreso) {
                boolean usuarioActivoEncontrado = false;

                for (DataSnapshot snapshot : dataSnapshotIngreso.getChildren()) {
                    String estado = snapshot.child("estado").getValue(String.class);

                    if ("activo".equals(estado)) {
                        usuarioActivoEncontrado = true;
                        // Usuario está activo, cargar datos del usuario
                        consultarCodUsuario(dataSnapshotIngreso);
                        break; // Deja de buscar cuando encuentras un usuario activo
                    }
                }

                if (!usuarioActivoEncontrado) {
                    // No hay usuarios activos encontrados, mostrar mensaje
                    Toast.makeText(RegistroSalidaActivity.this, "Usuario no ingreso a Cibertec", Toast.LENGTH_SHORT).show();
                    clearInputs();
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
    private void consultarDocumento(String numDocumento) {
        String numDocumentoUpper = numDocumento.toUpperCase();

        // Verificar si el usuario ya existe en la tabla Ingreso
        databaseReference.child("Salida")
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
                                    Toast.makeText(RegistroSalidaActivity.this, "Usuario ya salió de Cibertec", Toast.LENGTH_SHORT).show();
                                    clearInputs();
                                }
                            }
                        } else {
                            // Si el usuario no está en Salida, consultar en las otras tablas
                            consultarNumDocumento(numDocumentoUpper);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroSalidaActivity.this, "Error al verificar el ingreso", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void consultarNumDocumento(String numDocumento) {
        // Verificar si el número de documento existe en alguna de las tablas de Firebase
        databaseReference.child("estudiante").orderByChild("numDocumento").equalTo(numDocumento)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            // No existe en "estudiante", verificar en "docente"
                            verificarNumDocumentoEnDocente(numDocumento);
                        } else {
                            // Documento encontrado en "estudiante", verificar en "Ingreso"
                            verificarNumDocumentoEnIngreso(dataSnapshot, numDocumento);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroSalidaActivity.this, "Error al consultar el documento", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verificarNumDocumentoEnDocente(String numDocumento) {
        databaseReference.child("docente").orderByChild("numDocumento").equalTo(numDocumento)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            // No existe en "docente", verificar en "personalAdministrativo"
                            verificarNumDocumentoEnPersonalAdministrativo(numDocumento);
                        } else {
                            // Documento encontrado en "docente", verificar en "Ingreso"
                            verificarNumDocumentoEnIngreso(dataSnapshot, numDocumento);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroSalidaActivity.this, "Error al consultar el documento", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verificarNumDocumentoEnPersonalAdministrativo(String numDocumento) {
        databaseReference.child("personalAdministrativo").orderByChild("numDocumento").equalTo(numDocumento)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            // El número de documento no existe en ninguna de las tablas
                            clearInputs(); // Limpiar todos los campos si el documento no existe
                            Toast.makeText(RegistroSalidaActivity.this, "Número de documento no válido", Toast.LENGTH_SHORT).show();
                        } else {
                            // Documento encontrado en "personalAdministrativo", verificar en "Ingreso"
                            verificarNumDocumentoEnIngreso(dataSnapshot, numDocumento);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroSalidaActivity.this, "Error al consultar el documento", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verificarNumDocumentoEnIngreso(DataSnapshot dataSnapshotUsuario, String numeroDocumento) {
        databaseReference.child("Ingreso").orderByChild("numeroDocumento").equalTo(numeroDocumento)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshotIngreso) {
                        boolean usuarioActivoEncontrado = false;

                        for (DataSnapshot snapshot : dataSnapshotIngreso.getChildren()) {
                            String estado = snapshot.child("estado").getValue(String.class);

                            if ("activo".equals(estado)) {
                                usuarioActivoEncontrado = true;
                                // Usuario está activo, cargar datos del usuario
                                cargarDatosNumDocumento(dataSnapshotIngreso);
                                break; // Deja de buscar cuando encuentras un usuario activo
                            }
                        }

                        if (!usuarioActivoEncontrado) {
                            // No hay usuarios activos encontrados, mostrar mensaje
                            Toast.makeText(RegistroSalidaActivity.this, "Usuario no ingreso a Cibertec", Toast.LENGTH_SHORT).show();
                            clearInputs();
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

    private void setSpinnerValue(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int position = adapter.getPosition(value); // Encuentra la posición del valor en el adaptador
        if (position >= 0) {
            spinner.setSelection(position); // Establece la posición en el spinner
        }
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
                    String estado = childSnapshot.child("estado").getValue(String.class);

                    if (codigo != null && codigo.equals(codigoUsuario) && "activo".equals(estado)) {
                        // Solo establece el valor de micromovilidad si el estado es activo
                        String micromovilidad = childSnapshot.child("tipoMicromovilidad").getValue(String.class);
                        txtMovilidad.setText(micromovilidad != null ? micromovilidad : "");
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // Si no se encontró un usuario activo, establece un valor predeterminado o deja el campo vacío
                    txtMovilidad.setText("");
                    Toast.makeText(RegistroSalidaActivity.this, "Micromovilidad no encontrada para usuario activo", Toast.LENGTH_SHORT).show();
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

        if (spinnerTipoUsuario.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Ingrese el tipo de usuario", Toast.LENGTH_SHORT).show();
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

        return true;
    }

    private boolean validarCampos() {
        String tipoUsuario = spinnerTipoUsuario.getSelectedItem() != null
                ? spinnerTipoUsuario.getSelectedItem().toString()
                : "";
        String codUsuario = txtCodigoUsuario.getText().toString().trim();
        String numDocumento = txtNumeroDocumento.getText().toString().trim();

        // Validar Código Usuario
        if (!codUsuario.isEmpty()) {
            // Verificar que tiene exactamente una letra inicial seguida de 9 números
            if (!codUsuario.matches("^[a-zA-ZÑñ]\\d{9}$")) {
                // Si no cumple con la estructura general
                Toast.makeText(this, "Formato de Código debe tener una letra y 9 números", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                // Verificar la letra inicial según el tipo de usuario
                if (tipoUsuario.equals("Estudiante") && !codUsuario.matches("^[Ii].*")) {
                    Toast.makeText(this, "Estudiante debe tener inicial I o i", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (tipoUsuario.equals("Docente") && !codUsuario.matches("^[Pp].*")) {
                    Toast.makeText(this, "Docente debe tener inicial P o p", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (tipoUsuario.equals("Personal Administrativo") && !codUsuario.matches("^[Aa].*")) {
                    Toast.makeText(this, "Personal Administrativo debe tener inicial A o a", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        // Validar Número de Documento
        String tipoDocumento = spinnerTipoDocumento.getSelectedItem() != null
                ? spinnerTipoDocumento.getSelectedItem().toString()
                : "";

        if (!numDocumento.isEmpty() && numDocumento.matches("\\d+")) {
            if (numDocumento.matches("0+")) {
                Toast.makeText(this, "El número de documento no puede ser solo ceros", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (tipoDocumento.equals("DNI") && !numDocumento.matches("\\d{8}")) {
                Toast.makeText(this, "El DNI debe tener 8 dígitos", Toast.LENGTH_SHORT).show();
                return false;
            } else if (tipoDocumento.equals("Carnet Ext.") && !numDocumento.matches("\\d{11}")) {
                Toast.makeText(this, "El Carnet de Extranjería debe tener 11 dígitos", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!tipoDocumento.equals("DNI") && !tipoDocumento.equals("Carnet Ext.")
                    && !(numDocumento.matches("\\d{8}") || numDocumento.matches("\\d{11}"))) {
                Toast.makeText(this, "El número de documento debe tener 8 o 11 dígitos", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    // MENSAJE SALIDA
    private void showSuccessDialog(String usuario, String tipoMicromovilidad) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Configurar diseño personalizado
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50); // Ajustar según necesidad
        layout.setGravity(Gravity.CENTER); // Asegurarse de centrar el contenido

        // Agregar ícono
        ImageView icon = new ImageView(this);
        icon.setImageResource(R.drawable.check); // Asegúrate de tener el recurso "check" en drawable
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                200, // Ancho personalizado (ajustar según necesidad)
                200  // Alto personalizado (ajustar según necesidad)
        );
        iconParams.gravity = Gravity.CENTER; // Centrar el ícono horizontalmente
        icon.setLayoutParams(iconParams);
        icon.setPadding(0, 0, 0, 20); // Espacio inferior entre ícono y texto
        layout.addView(icon);

        // Agregar mensaje
        TextView message = new TextView(this);
        message.setText("¡Salida registrada exitosamente!");
        message.setTextSize(18);
        message.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        message.setTypeface(null, Typeface.BOLD);
        layout.addView(message);

        // Detalles adicionales
        TextView details = new TextView(this);
        details.setText("Salió de Cibertec\n\nUsuario: " + usuario + "\nTipo de micromovilidad: " + tipoMicromovilidad);
        details.setTextSize(16);
        details.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        layout.addView(details);

        // Configurar AlertDialog
        builder.setView(layout)
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
        imgView.setImageResource(R.drawable.ic_launcher_foreground);
    }

    private void clearDocumento() {
        txtUsuario.setText("");
        txtMovilidad.setText("");
        txtCodigoUsuario.setText("");
        spinnerTipoUsuario.setSelection(0);
        spinnerTipoDocumento.setSelection(0);
        imgView.setImageResource(R.drawable.ic_launcher_foreground);
    }

    private void clearCodigo() {
        txtUsuario.setText("");
        txtMovilidad.setText("");
        spinnerTipoUsuario.setSelection(0);
        spinnerTipoDocumento.setSelection(0);
        imgView.setImageResource(R.drawable.ic_launcher_foreground);
    }

}