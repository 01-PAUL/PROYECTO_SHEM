package com.example.proyecto_shem.vista;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_shem.R;
import com.example.proyecto_shem.adapter.HistorialIngresoAdapter;
import com.example.proyecto_shem.entity.Ingreso;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class HistorialIngresoUsuarioActivity extends AppCompatActivity {

    Button btnRegresar;
    EditText txtFechaIngreso;
    TextView nomUsuarioSelect, tvRegistroCount, tvErrorMsg;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    HistorialIngresoAdapter historialIngresoAdapter;
    ArrayList<Ingreso> list;
    ArrayList<Ingreso> originalList; // Lista original sin filtrar
    private String codigoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_ingreso_usuario);

        // Inicialización de las vistas
        txtFechaIngreso = findViewById(R.id.txtFechaIngreso);
        tvRegistroCount = findViewById(R.id.tvRegistroCount);
        nomUsuarioSelect = findViewById(R.id.nomUsuarioSelect);
        tvErrorMsg = findViewById(R.id.tv_error_msg);
        recyclerView = findViewById(R.id.historial);

        // Obtener el nombre de usuario desde el Intent
        String nombreUsuario = getIntent().getStringExtra("nombreUsuario");
        nomUsuarioSelect.setText("USUARIO: " + nombreUsuario);

        // Inicialización de Firebase y RecyclerView
        databaseReference = FirebaseDatabase.getInstance().getReference("Ingreso");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        originalList = new ArrayList<>();
        historialIngresoAdapter = new HistorialIngresoAdapter(this, list);
        recyclerView.setAdapter(historialIngresoAdapter);

        // Cargar todo el historial al inicio
        cargarTodosLosDatos();

        btnRegresar = findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(v -> finish());

        // Configurar el evento para la selección de fecha
        txtFechaIngreso.setOnClickListener(v -> mostrarCalendarioYFiltrar(nombreUsuario));


        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            codigoUsuario = bundle.getString("codigoUsuario");
            nomUsuarioSelect.setText(bundle.getString("usuario"));
            cargarDatosEspecificos(codigoUsuario);
        } else {
            // Si no hay código de usuario, cargar todos los datos
            cargarTodosLosDatos();
        }
    }
    private void mostrarCalendarioYFiltrar(String codigoUsuario) {
        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Crear un DatePickerDialog con fecha máxima como la fecha actual
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, dayOfMonth) -> {
            String fechaSeleccionada = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, dayOfMonth);
            txtFechaIngreso.setText(fechaSeleccionada);
            cargarDatosFiltrados(fechaSeleccionada);
        }, year, month, day);

        // Establecer la fecha máxima (hoy) para deshabilitar fechas futuras
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void cargarDatosFiltrados(String fechaSeleccionada) {
        // Limpiar la lista para el nuevo filtro
        list.clear();

        // Comparar la fecha seleccionada con cada fecha de ingreso en originalList
        for (Ingreso ingreso : originalList) {
            if (fechaSeleccionada.equals(ingreso.getFechaIngreso())) {
                list.add(ingreso);
            }
        }

        historialIngresoAdapter.notifyDataSetChanged();
        actualizarContadorRegistros();

        // Mostrar mensaje si no hay resultados
        if (list.isEmpty()) {
            tvErrorMsg.setText("No se encontró ningun registro !!!!!");
            tvErrorMsg.setVisibility(View.VISIBLE);
        } else {
            tvErrorMsg.setVisibility(View.GONE);
        }
    }

    private void actualizarContadorRegistros() {
        tvRegistroCount.setText("Total de registros: " + list.size());
        tvRegistroCount.setVisibility(View.VISIBLE);
    }


    private void cargarTodosLosDatos() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                originalList.clear(); // Limpia la lista original antes de agregar nuevos datos

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Ingreso ingreso = dataSnapshot.getValue(Ingreso.class);
                    if (ingreso != null) {
                        list.add(ingreso);
                        originalList.add(ingreso); // Guardar en la lista original también
                    }
                }

                historialIngresoAdapter.notifyDataSetChanged();
                actualizarContadorRegistros();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistorialIngresoUsuarioActivity.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void cargarDatosEspecificos(String codigoUsuario) {
        databaseReference.orderByChild("codigoUsuario").equalTo(codigoUsuario)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        originalList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Ingreso ingreso = dataSnapshot.getValue(Ingreso.class);
                            list.add(ingreso);
                            originalList.add(ingreso); // Guardar en la lista original también
                        }
                        actualizarContadorRegistros();
                        historialIngresoAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HistorialIngresoUsuarioActivity.this, "Error al obtener datos específicos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}