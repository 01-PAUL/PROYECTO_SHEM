package com.example.proyecto_shem.vista;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistroPermisoActivity extends AppCompatActivity {

    TextView txtUsuario, txtGenero, txtFechNacim;
    Spinner spinnerTipoDocumento, spinnerArea, spinnerMicromovilidad;
    Button btnConsultar, btnRegistrar, btnRegresar;
    ImageView imgView;
    EditText txtDetalle, txtNumeroDocumento;

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
        spinnerArea = findViewById(R.id.spinnerArea);
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
                if (s.length() > 0) {
                    btnRegistrar.setEnabled(true);
                    btnRegistrar.setBackgroundColor(Color.parseColor("#033657"));
                } else {
                    btnRegistrar.setEnabled(false);
                    btnRegistrar.setBackgroundColor(0xFFA9A9A9);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        txtDetalle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita implementar
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No se necesita implementar
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();

                // Si el texto está vacío, no validar
                if (text.isEmpty()) {
                    txtDetalle.setError(null); // Limpia cualquier error si el campo está vacío
                    disableRegisterButton();
                    return;
                }

                boolean isValid = true;
                String errorMessage = null;

                // Contar solo las letras (ignorando espacios)
                int letterCount = countLetters(text);

                // Validar que no empiece con espacios, permita espacios después de letras, tenga entre 6 y 35 letras y no repita más de dos letras consecutivas
                if (!text.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+( [a-zA-ZáéíóúÁÉÍÓÚñÑ]+)*$")) {
                    isValid = false;
                    errorMessage = "Solo se permiten letras y espacios.";
                } else if (letterCount < 6) {
                    isValid = false;
                    errorMessage = "Debe tener al menos 6 letras.";
                } else if (letterCount > 35) {
                    isValid = false;
                    errorMessage = "No debe exceder las 35 letras.";
                } else if (text.matches(".([a-zA-ZáéíóúÁÉÍÓÚñÑ])\\1\\1.")) { // Detecta más de dos letras iguales seguidas
                    isValid = false;
                    errorMessage = "No se permiten más de dos letras iguales juntas.";
                }

                // Si no es válido, mostrar mensaje de error y limpiar el texto si es necesario
                if (!isValid) {
                    String cleanedText = text;

                    if (letterCount > 35) {
                        errorMessage = "No se permite más de 35 letras.";
                        // Recorta al máximo permitido
                        cleanedText = trimToMaxLetters(text, 35);
                    }

                    cleanedText = cleanedText.replaceAll("^\\s+", "") // Elimina espacios iniciales
                            .replaceAll("\\s{2,}", " ") // Reemplaza múltiples espacios por uno
                            .replaceAll("([a-zA-ZáéíóúÁÉÍÓÚñÑ])\\1\\1+", "$1$1"); // Reemplaza más de dos letras consecutivas por dos

                    txtDetalle.removeTextChangedListener(this); // Evita el bucle infinito
                    txtDetalle.setText(cleanedText);
                    txtDetalle.setSelection(cleanedText.length()); // Mantén el cursor al final del texto
                    txtDetalle.addTextChangedListener(this);

                    // Mostrar mensaje de error
                    txtDetalle.setError(errorMessage);
                    disableRegisterButton(); // Deshabilita el botón si no es válido
                } else {
                    txtDetalle.setError(null); // Limpia el error si todo es válido
                    enableRegisterButton(); // Habilita el botón si es válido
                }
            }

            // Método auxiliar para contar letras en el texto (ignora los espacios)
            private int countLetters(String text) {
                int count = 0;
                for (char c : text.toCharArray()) {
                    if (Character.isLetter(c)) {
                        count++;
                    }
                }
                return count;
            }

            // Método auxiliar para recortar el texto al máximo de letras permitidas
            private String trimToMaxLetters(String text, int maxLetters) {
                StringBuilder result = new StringBuilder();
                int letterCount = 0;

                for (char c : text.toCharArray()) {
                    if (Character.isLetter(c)) {
                        if (letterCount >= maxLetters) {
                            break;
                        }
                        letterCount++;
                    }
                    result.append(c);
                }

                return result.toString();
            }

            // Método para deshabilitar el botón de registrar
            private void disableRegisterButton() {
                btnRegistrar.setEnabled(false);
                btnRegistrar.setBackgroundColor(0xFFA9A9A9); // Puedes cambiar el color según tu diseño
            }

            // Método para habilitar el botón de registrar
            private void enableRegisterButton() {
                btnRegistrar.setEnabled(true);
                btnRegistrar.setBackgroundColor(Color.parseColor("#033657")); // Puedes cambiar el color según tu diseño
            }
        });





        txtNumeroDocumento.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No es necesario implementar
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
            }
        });

        spinnerTipoDocumento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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

        // Configuración de botones
        btnRegresar.setOnClickListener(v -> finish());

        btnConsultar.setOnClickListener(v -> {
            // Obtener el número de documento y verificar si está vacío
            String numDocumento = txtNumeroDocumento.getText().toString().trim();

            // Si el número de documento está vacío, limpiar toda la información
            if (numDocumento.isEmpty()) {
                // Limpiar TextViews
                txtUsuario.setText("");
                txtGenero.setText("");
                txtFechNacim.setText("");
                imgView.setImageResource(R.drawable.ic_launcher_foreground);

                // Mostrar un mensaje indicando que el campo está vacío
                Toast.makeText(RegistroPermisoActivity.this, "Ingrese el N° Documento", Toast.LENGTH_SHORT).show();
            } else {
                // Si no está vacío, proceder con la consulta
                consultarDocumento(numDocumento);
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
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            String estado = snapshot.child("estado").getValue(String.class);

                                            if ("inactivo".equals(estado)) {
                                                // El usuario puede volver a ingresar
                                                registrarPermiso(numDocumento);
                                            } else {
                                                // Usuario aún está activo en la tabla Ingreso
                                                // El usuario ya ingresó, mostrar mensaje
                                                Toast.makeText(RegistroPermisoActivity.this, "Usuario ya ingresó a Cibertec", Toast.LENGTH_SHORT).show();
                                            }
                                        }
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
        String area = spinnerArea.getSelectedItem().toString();
        String imageUrl = imgView.getTag() != null ? imgView.getTag().toString() : "";

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String fechaIngreso = dateFormat.format(calendar.getTime());
        String horaIngreso = timeFormat.format(calendar.getTime());

        Map<String, Object> datosPermiso = new HashMap<>();
        datosPermiso.put("tipoDocumento", tipoDocumento);
        datosPermiso.put("numeroDocumento", numDocumento);
        datosPermiso.put("usuario", usuario);
        datosPermiso.put("genero", genero);
        datosPermiso.put("fechNacim", fechNacim);
        datosPermiso.put("detallePermiso", detalle);
        datosPermiso.put("tipoMicromovilidad", tipoMicromovilidad);
        datosPermiso.put("area", area);
        datosPermiso.put("imageUrl", imageUrl);
        datosPermiso.put("fechaIngreso", fechaIngreso);
        datosPermiso.put("horaIngreso", horaIngreso);
        datosPermiso.put("estado", "activo"); // Añadir el campo de estado

        databaseReference.child("Permiso Salida")
                .orderByChild("numeroDocumento")
                .equalTo(numDocumento)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().child("estado").setValue("inactivo"); // Cambiar estado a inactivo
                        }

                        databaseReference.child("Permiso Ingreso").push().setValue(datosPermiso)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        showSuccessDialog(usuario, tipoMicromovilidad);
                                    } else {
                                        Toast.makeText(RegistroPermisoActivity.this, "Error al registrar permiso", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroPermisoActivity.this, "Error al verificar estado en Salida", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadSpinnerData() {
        loadSpinnerFromFirebase(databaseReference.child("tipoDocumento"), spinnerTipoDocumento);
        loadSpinnerFromFirebase(databaseReference.child("tipoMicromovilidad"), spinnerMicromovilidad);
        loadSpinnerFromFirebase(databaseReference.child("area"), spinnerArea);
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

    private void consultarDocumento(String numDocumento) {
        String numDocumentoUpper = numDocumento.toUpperCase();

        // Verificar si el usuario ya existe en la tabla Ingreso
        databaseReference.child("Permiso Ingreso")
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
                            Toast.makeText(RegistroPermisoActivity.this, "Usuario ya ingresó a Cibertec", Toast.LENGTH_SHORT).show();
                            clearInputs();
                        } else {
                            // No se encontró un registro activo, el usuario puede ingresar de nuevo
                            consultarNumDocumento(numDocumentoUpper);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegistroPermisoActivity.this, "Error al verificar el ingreso", Toast.LENGTH_SHORT).show();
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
                        } else {
                            // Mostrar mensaje si no se encontró en ninguna de las tablas
                            Toast.makeText(RegistroPermisoActivity.this, "N° de documento no existe", Toast.LENGTH_SHORT).show();
                            clearInputs();
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

    // MENSAJE REGISTRADO
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
        message.setText("¡Permiso registrado exitosamente!");
        message.setTextSize(18);
        message.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        message.setTypeface(null, Typeface.BOLD);
        layout.addView(message);

        // Detalles adicionales
        TextView details = new TextView(this);
        details.setText("Ingreso a Cibertec\n\nUsuario: " + usuario + "\nTipo de micromovilidad: " + tipoMicromovilidad);
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

    //VALIDACIONES
    private boolean validateInputs() {
        // Validación de otros campos como el área, detalle, etc.
        if (spinnerArea.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione un área", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (txtDetalle.getText().toString().isEmpty()) {
            Toast.makeText(this, "Ingrese el detalle", Toast.LENGTH_SHORT).show();
            return false;
        }

        String detalle = txtDetalle.getText().toString().trim().replaceAll("\\s{2,}", " ");
        if (detalle.isEmpty() || !detalle.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            Toast.makeText(this, "Ingrese un detalle válido (solo letras)", Toast.LENGTH_SHORT).show();
            return false;
        }
        txtDetalle.setText(detalle); // Asegura que el texto limpio quede en el campo.

        if (spinnerMicromovilidad.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione un tipo de micromovilidad", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }





    private boolean validarCampos() {
        String tipoDocumento = spinnerTipoDocumento.getSelectedItem().toString();
        String numDocumento = txtNumeroDocumento.getText().toString().trim();

        // Validación si el campo de documento está vacío
        if (numDocumento.isEmpty()) {
            Toast.makeText(this, "El campo de documento no puede estar vacío", Toast.LENGTH_SHORT).show();
            return false; // Retorna false si el campo está vacío
        }

        // Validación para evitar que el número sea solo ceros
        if (numDocumento.matches("0+")) {
            Toast.makeText(this, "El número de documento no puede ser solo ceros", Toast.LENGTH_SHORT).show();
            return false; // Retorna false si el número es solo ceros
        }

        // Validación del número de documento según el tipo seleccionado
        if (tipoDocumento.equals("DNI")) {
            if (!numDocumento.matches("\\d{8}")) {
                Toast.makeText(this, "El DNI debe tener 8 dígitos", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (tipoDocumento.equals("Carnet Ext.")) {
            if (!numDocumento.matches("\\d{11}")) {
                Toast.makeText(this, "El Carnet de Extranjería debe tener 11 dígitos", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            // Si no se selecciona un tipo, validar que sea 8 o 11 dígitos
            if (!(numDocumento.matches("\\d{8}") || numDocumento.matches("\\d{11}"))) {
                Toast.makeText(this, "El número de documento debe tener 8 o 11 dígitos", Toast.LENGTH_SHORT).show();
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
        spinnerArea.setSelection(0);
        imgView.setImageResource(R.drawable.ic_launcher_foreground);
    }
}