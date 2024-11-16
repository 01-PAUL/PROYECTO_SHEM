package com.example.proyecto_shem.vista;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_shem.R;
import com.example.proyecto_shem.adapter.PermisoSalidaAdapter;
import com.example.proyecto_shem.entity.PermisoSalida;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConsultaPermisoSalidaActivity extends AppCompatActivity {
    private Spinner spinnerArea;
    private TextView tvRegistroCount, tvErrorMsg;
    private Button btnRegresar;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private PermisoSalidaAdapter permisoSalidaAdapter;
    private ArrayList<PermisoSalida> list;
    private ArrayList<PermisoSalida> listOriginal; // Lista para mantener todos los registros

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_permiso_salida);

        // Inicialización de las vistas
        spinnerArea = findViewById(R.id.spinnerArea);
        tvRegistroCount = findViewById(R.id.tvRegistroCount);
        tvErrorMsg = findViewById(R.id.tv_error_msg);
        btnRegresar = findViewById(R.id.btnRegresar);
        recyclerView = findViewById(R.id.datoLista);

        // Configurar Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Permiso Salida");

        // Configurar RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        listOriginal = new ArrayList<>(); // Inicializar la lista original
        permisoSalidaAdapter = new PermisoSalidaAdapter(this, list);
        recyclerView.setAdapter(permisoSalidaAdapter);

        // Cargar datos y spinner
        cargarTodosLosDatos();
        loadSpinnerAreaData();

        // Configuración del botón Regresar
        btnRegresar.setOnClickListener(v -> finish());
    }

    private void loadSpinnerAreaData() {
        // Acceder directamente al nodo "area"
        DatabaseReference areaReference = FirebaseDatabase.getInstance().getReference("area");
        areaReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> areaItems = new ArrayList<>();
                areaItems.add("Seleccione:");  // Item inicial

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String area = snapshot.getValue(String.class); // Obtener el valor como String
                        if (area != null) {
                            areaItems.add(area); // Agregar al ArrayList
                        }
                    }

                    // Configurar ArrayAdapter para el Spinner
                    ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(ConsultaPermisoSalidaActivity.this, android.R.layout.simple_spinner_item, areaItems);
                    areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Asignar el ArrayAdapter al Spinner
                    spinnerArea.setAdapter(areaAdapter);

                    // Configurar el listener del Spinner para filtrar los datos
                    spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedArea = parent.getItemAtPosition(position).toString();
                            if (!selectedArea.equals("Seleccione:")) {
                                filtrarPorArea(selectedArea); // Filtrar por área seleccionada
                            } else {
                                // Si "Seleccione:" es seleccionado, mostrar el último registro de cada usuario
                                mostrarUltimosRegistrosDeUsuarios();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // No hacer nada
                        }
                    });
                } else {
                    Toast.makeText(ConsultaPermisoSalidaActivity.this, "No se encontraron áreas en Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConsultaPermisoSalidaActivity.this, "Error al cargar áreas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarUltimosRegistrosDeUsuarios() {
        // Eliminar registros duplicados, manteniendo solo el más reciente por usuario
        Map<String, PermisoSalida> registroRecientePorUsuario = new HashMap<>();

        for (PermisoSalida permiso : listOriginal) {
            String usuario = permiso.getUsuario();
            // Verifica si el usuario ya existe, si no, agrégalo con el permiso
            if (!registroRecientePorUsuario.containsKey(usuario)) {
                registroRecientePorUsuario.put(usuario, permiso);
            } else {
                PermisoSalida registroExistente = registroRecientePorUsuario.get(usuario);
                int comparacionFecha = permiso.getFechaSalida().compareTo(registroExistente.getFechaSalida());

                // Si la fecha es más reciente, actualiza el registro
                if (comparacionFecha > 0) {
                    registroRecientePorUsuario.put(usuario, permiso);
                } else if (comparacionFecha == 0) {
                    // Si las fechas son iguales, compara las horas
                    int comparacionHora = permiso.getHoraSalida().compareTo(registroExistente.getHoraSalida());
                    if (comparacionHora > 0) {
                        registroRecientePorUsuario.put(usuario, permiso);
                    }
                }
            }
        }

        // Actualizar la lista con solo el último registro por usuario
        ArrayList<PermisoSalida> ultimosRegistros = new ArrayList<>(registroRecientePorUsuario.values());

        // Actualizar RecyclerView con los datos filtrados
        list.clear();
        list.addAll(ultimosRegistros);

        // Actualizar contador de registros
        tvRegistroCount.setText("Total de registros: " + ultimosRegistros.size());
        tvRegistroCount.setVisibility(View.VISIBLE);
        tvErrorMsg.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        permisoSalidaAdapter.notifyDataSetChanged();
    }

    private void filtrarPorArea(String selectedArea) {
        // Filtrar los registros por el área seleccionada
        ArrayList<PermisoSalida> registrosFiltrados = new ArrayList<>();

        for (PermisoSalida permiso : listOriginal) {
            if (permiso.getArea() != null && permiso.getArea().equals(selectedArea)) {
                registrosFiltrados.add(permiso);
            }
        }

        // Eliminar registros duplicados, manteniendo solo el más reciente por usuario
        Map<String, PermisoSalida> registroRecientePorUsuario = new HashMap<>();

        for (PermisoSalida permiso : registrosFiltrados) {
            String usuario = permiso.getUsuario();
            // Verifica si el usuario ya existe, si no, agrégalo con el permiso
            if (!registroRecientePorUsuario.containsKey(usuario)) {
                registroRecientePorUsuario.put(usuario, permiso);
            } else {
                PermisoSalida registroExistente = registroRecientePorUsuario.get(usuario);
                int comparacionFecha = permiso.getFechaSalida().compareTo(registroExistente.getFechaSalida());

                // Si la fecha es más reciente, actualiza el registro
                if (comparacionFecha > 0) {
                    registroRecientePorUsuario.put(usuario, permiso);
                } else if (comparacionFecha == 0) {
                    // Si las fechas son iguales, compara las horas
                    int comparacionHora = permiso.getHoraSalida().compareTo(registroExistente.getHoraSalida());
                    if (comparacionHora > 0) {
                        registroRecientePorUsuario.put(usuario, permiso);
                    }
                }
            }
        }

        // Actualizar la lista con solo el último registro por usuario
        ArrayList<PermisoSalida> ultimosRegistros = new ArrayList<>(registroRecientePorUsuario.values());

        // Si no hay registros para mostrar, mostrar mensaje de error
        if (ultimosRegistros.isEmpty()) {
            tvErrorMsg.setVisibility(View.VISIBLE);
            tvErrorMsg.setText("No se encontraron registros para el área seleccionada");
            recyclerView.setVisibility(View.GONE);
        } else {
            tvErrorMsg.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        // Actualizar RecyclerView con los datos filtrados
        list.clear();
        list.addAll(ultimosRegistros);

        // Actualizar contador de registros
        tvRegistroCount.setText("Total de registros: " + ultimosRegistros.size());
        tvRegistroCount.setVisibility(View.VISIBLE);

        permisoSalidaAdapter.notifyDataSetChanged();
    }

    private void cargarTodosLosDatos() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                listOriginal.clear(); // Limpiar ambas listas

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PermisoSalida permiso = dataSnapshot.getValue(PermisoSalida.class);
                    list.add(permiso);
                    listOriginal.add(permiso); // Guardar los registros originales
                }

                permisoSalidaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ConsultaPermisoSalidaActivity.this, "Error al cargar los permisos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}