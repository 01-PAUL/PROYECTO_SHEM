package com.example.proyecto_shem.vista;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_shem.R;
import com.example.proyecto_shem.adapter.IngresoAdapter;
import com.example.proyecto_shem.adapter.SalidaAdapter;
import com.example.proyecto_shem.entity.Ingreso;
import com.example.proyecto_shem.entity.Salida;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConsultaSalidaActivity extends AppCompatActivity {

    EditText txtNombreUsuario;
    TextView tvRegistroCount, tvErrorMsg;
    Button  btnRegresar;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    SalidaAdapter salidaAdapter;
    ArrayList<Salida> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_salida);

        // Inicialización de las vistas
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        tvRegistroCount = findViewById(R.id.tvRegistroCount);
        tvErrorMsg = findViewById(R.id.tv_error_msg);
        btnRegresar = findViewById(R.id.btnRegresar);
        recyclerView = findViewById(R.id.datoLista);

        databaseReference = FirebaseDatabase.getInstance().getReference("Salida");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        salidaAdapter = new SalidaAdapter(this, list);
        recyclerView.setAdapter(salidaAdapter);

        cargarTodosLosDatos();

        // Configuración del botón Regresar
        btnRegresar.setOnClickListener(v -> finish());

        // Validación de solo letras (sin espacios ni otros caracteres) en el campo de texto
        txtNombreUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textoIngresado = s.toString().trim();

                if (textoIngresado.isEmpty()) {
                    // Oculta el mensaje de error y restablece el borde al diseño principal
                    tvErrorMsg.setVisibility(View.GONE);
                    txtNombreUsuario.setBackgroundResource(R.drawable.edittext_border_white); // Borde predeterminado
                    txtNombreUsuario.setCompoundDrawables(null, null, null, null); // Quita el ícono de error
                    cargarTodosLosDatos(); // Vuelve a cargar todos los datos
                } else if (!textoIngresado.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+$")) {
                    // Muestra el mensaje de error si contiene caracteres no permitidos o espacios
                    mostrarError("Solo se permiten letras sin espacios");
                } else {
                    tvErrorMsg.setVisibility(View.GONE);
                    txtNombreUsuario.setBackgroundResource(R.drawable.default_border); // Borde predeterminado
                    txtNombreUsuario.setCompoundDrawables(null, null, null, null); // Quita el ícono de error
                    consultarUsuario(textoIngresado); // Realiza la consulta
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
                    Salida salida = dataSnapshot.getValue(Salida.class);
                    list.add(salida);
                }
                filtrarRegistrosDuplicadosPorFecha();
                salidaAdapter.notifyDataSetChanged();
                actualizarContadorRegistros();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ConsultaSalidaActivity.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
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
                    Salida salida = dataSnapshot.getValue(Salida.class);

                    if (salida != null && salida.getUsuario().toLowerCase(Locale.ROOT).contains(nombreUsuarioLower)) {
                        list.add(salida);
                    }
                }
                filtrarRegistrosDuplicadosPorFecha();
                if (list.isEmpty()) {
                    mostrarError("Usuario '" + nombreUsuario + "' no encontrado");
                    Toast.makeText(ConsultaSalidaActivity.this, "Usuario '" + nombreUsuario + "' no encontrado", Toast.LENGTH_SHORT).show(); // Alerta de usuario no encontrado
                }
                salidaAdapter.notifyDataSetChanged();
                actualizarContadorRegistros();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ConsultaSalidaActivity.this, "Error al consultar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarContadorRegistros() {
        tvRegistroCount.setText("Total de registros: " + list.size());
        tvRegistroCount.setVisibility(View.VISIBLE);
    }

    private void filtrarRegistrosDuplicadosPorFecha() {
        Map<String, Salida> registroRecientePorUsuario = new HashMap<>();

        for (Salida salida : list) {
            String usuario = salida.getUsuario();
            if (!registroRecientePorUsuario.containsKey(usuario)) {
                registroRecientePorUsuario.put(usuario, salida);
            } else {
                Salida registroExistente = registroRecientePorUsuario.get(usuario);
                int comparacionFecha = salida.getFechaSalida().compareTo(registroExistente.getFechaSalida());

                if (comparacionFecha > 0) {
                    registroRecientePorUsuario.put(usuario, salida);
                } else if (comparacionFecha == 0) {
                    int comparacionHora = salida.getHoraSalida().compareTo(registroExistente.getHoraSalida());
                    if (comparacionHora > 0) {
                        registroRecientePorUsuario.put(usuario, salida);
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

        // Cambia el color del borde del campo de texto a rojo cuando hay un error
        txtNombreUsuario.setBackgroundResource(R.drawable.error_border); // Borde rojo personalizado

        // Configurar el ícono de advertencia personalizado solo cuando haya un error
        Drawable iconoError = ContextCompat.getDrawable(this, R.drawable.valida); // Asegúrate de tener este ícono en drawable
        if (iconoError != null) {
            iconoError.setBounds(0, 0, iconoError.getIntrinsicWidth(), iconoError.getIntrinsicHeight());
            txtNombreUsuario.setCompoundDrawables(null, null, iconoError, null);
        }
    }
}