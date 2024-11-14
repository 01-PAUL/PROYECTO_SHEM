package com.example.proyecto_shem.vista;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_shem.R;
import com.example.proyecto_shem.adapter.IngresoAdapter;
import com.example.proyecto_shem.entity.Ingreso;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConsultaIngresoActivity extends AppCompatActivity {

    EditText txtNombreUsuario;
    TextView tvRegistroCount, tvErrorMsg;
    Button btnRegresar;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    IngresoAdapter ingresoAdapter;
    ArrayList<Ingreso> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_ingreso);

        // Inicialización de las vistas
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        tvRegistroCount = findViewById(R.id.tvRegistroCount);
        tvErrorMsg = findViewById(R.id.tv_error_msg);
        btnRegresar = findViewById(R.id.btnRegresar);
        recyclerView = findViewById(R.id.datoLista);

        databaseReference = FirebaseDatabase.getInstance().getReference("Ingreso");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        ingresoAdapter = new IngresoAdapter(this, list);
        recyclerView.setAdapter(ingresoAdapter);

        cargarTodosLosDatos();

        // Configuración del botón Regresar
        btnRegresar.setOnClickListener(v -> finish());

        // Validación de solo letras en el campo de texto
        txtNombreUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textoIngresado = s.toString().replaceAll("\\s{2,}", " "); // Reemplazar múltiples espacios consecutivos por uno solo

                // Verificar si el primer carácter es un espacio
                if (textoIngresado.startsWith(" ")) {
                    // Eliminar el espacio al principio
                    textoIngresado = textoIngresado.substring(1);
                    txtNombreUsuario.setText(textoIngresado);
                    txtNombreUsuario.setSelection(textoIngresado.length()); // Colocar el cursor al final
                    return; // No continuar con la validación adicional
                }

                // Validaciones adicionales
                if (textoIngresado.isEmpty()) {
                    tvErrorMsg.setVisibility(View.GONE);
                    txtNombreUsuario.setBackgroundResource(R.drawable.edittext_border_white);
                    txtNombreUsuario.setCompoundDrawables(null, null, null, null);
                    cargarTodosLosDatos();
                } else if (!textoIngresado.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) { // Permitir espacios después de ingresar letras
                    mostrarError("Solo se permiten letras");
                } else {
                    tvErrorMsg.setVisibility(View.GONE);
                    txtNombreUsuario.setBackgroundResource(R.drawable.default_border);
                    txtNombreUsuario.setCompoundDrawables(null, null, null, null);
                    consultarUsuario(textoIngresado);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


    }

    private void cargarTodosLosDatos() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Ingreso ingreso = dataSnapshot.getValue(Ingreso.class);
                    list.add(ingreso);
                }
                filtrarRegistrosDuplicadosPorFecha();
                ingresoAdapter.notifyDataSetChanged();
                actualizarContadorRegistros();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ConsultaIngresoActivity.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void consultarUsuario(String nombreUsuario) {
        String nombreUsuarioLower = nombreUsuario.toLowerCase(Locale.ROOT);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Ingreso ingreso = dataSnapshot.getValue(Ingreso.class);

                    if (ingreso != null && ingreso.getUsuario().toLowerCase(Locale.ROOT).contains(nombreUsuarioLower)) {
                        list.add(ingreso);
                    }
                }
                filtrarRegistrosDuplicadosPorFecha();
                if (list.isEmpty()) {
                    mostrarError("Usuario '" + nombreUsuario + "' no encontrado");
                }
                ingresoAdapter.notifyDataSetChanged();
                actualizarContadorRegistros();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ConsultaIngresoActivity.this, "Error al consultar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarContadorRegistros() {
        tvRegistroCount.setText("Total de registros: " + list.size());
        tvRegistroCount.setVisibility(View.VISIBLE);
    }

    private void filtrarRegistrosDuplicadosPorFecha() {
        Map<String, Ingreso> registroRecientePorUsuario = new HashMap<>();

        for (Ingreso ingreso : list) {
            String usuario = ingreso.getUsuario();
            if (!registroRecientePorUsuario.containsKey(usuario)) {
                registroRecientePorUsuario.put(usuario, ingreso);
            } else {
                Ingreso registroExistente = registroRecientePorUsuario.get(usuario);
                int comparacionFecha = ingreso.getFechaIngreso().compareTo(registroExistente.getFechaIngreso());

                if (comparacionFecha > 0) {
                    registroRecientePorUsuario.put(usuario, ingreso);
                } else if (comparacionFecha == 0) {
                    int comparacionHora = ingreso.getHoraIngreso().compareTo(registroExistente.getHoraIngreso());
                    if (comparacionHora > 0) {
                        registroRecientePorUsuario.put(usuario, ingreso);
                    }
                }
            }
        }

        list.clear();
        list.addAll(registroRecientePorUsuario.values());
    }

    private void mostrarError(String mensaje) {
        tvErrorMsg.setText(mensaje);
        tvErrorMsg.setVisibility(View.VISIBLE);
        txtNombreUsuario.setBackgroundResource(R.drawable.error_border);

        Drawable iconoError = ContextCompat.getDrawable(this, R.drawable.valida);
        if (iconoError != null) {
            iconoError.setBounds(0, 0, iconoError.getIntrinsicWidth(), iconoError.getIntrinsicHeight());
            txtNombreUsuario.setCompoundDrawables(null, null, iconoError, null);
        }
    }
}