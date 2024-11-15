package com.example.proyecto_shem.vista;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_shem.R;
import com.example.proyecto_shem.adapter.HistorialSalidaAdapter;
import com.example.proyecto_shem.adapter.SalidaAdapter;
import com.example.proyecto_shem.entity.Salida;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class HistorialSalidaActivity extends AppCompatActivity {

    Button btnRegresar;
    EditText txtFechaIngreso;
    TextView tvRegistroCount, tvErrorMsg, tv_usuario;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    HistorialSalidaAdapter historialSalidaAdapter;
    ArrayList<Salida> list;
    ArrayList<Salida> originalList;
    String codigoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial_salida);

        btnRegresar = findViewById(R.id.btnRegresar);
        txtFechaIngreso = findViewById(R.id.txtFechaIngreso);
        tv_usuario = findViewById(R.id.tv_usuario);
        tvErrorMsg = findViewById(R.id.tv_error_msg);
        tvRegistroCount = findViewById(R.id.tvRegistroCount);
        btnRegresar = findViewById(R.id.btnRegresar);
        recyclerView = findViewById(R.id.historial);

        databaseReference = FirebaseDatabase.getInstance().getReference("Salida");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        originalList = new ArrayList<>();
        historialSalidaAdapter = new HistorialSalidaAdapter(this, list);
        recyclerView.setAdapter(historialSalidaAdapter);

        // Obtener el código de usuario desde el Intent
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            codigoUsuario = bundle.getString("codigoUsuario");
            tv_usuario.setText(bundle.getString("usuario"));
            cargarDatosEspecificos(codigoUsuario);
        } else {
            // Si no hay código de usuario, cargar todos los datos
            cargarTodosLosDatos();
        }

        txtFechaIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                HistorialSalidaActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int dayOfMonth) {
                        String fechaSeleccionada = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, dayOfMonth);
                        txtFechaIngreso.setText(fechaSeleccionada);
                        cargarDatosFiltrados(fechaSeleccionada);
                    }
                },
                year, month, day
        );

        // Establecer la fecha máxima (hoy) para deshabilitar fechas futuras
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void cargarDatosFiltrados(String fechaSeleccionada) {
        // Limpiar la lista para el nuevo filtro
        list.clear();

        // Comparar la fecha seleccionada con cada fecha de salida en originalList
        for (Salida salida : originalList) {
            if (fechaSeleccionada.equals(salida.getFechaSalida())) {
                list.add(salida);
            }
        }

        historialSalidaAdapter.notifyDataSetChanged();
        actualizarContadorRegistros();

        // Mostrar mensaje si no hay resultados
        if (list.isEmpty()) {
            tvErrorMsg.setText("No se encontró ningun registro !!!!!");
            tvErrorMsg.setVisibility(View.VISIBLE);
        } else {
            tvErrorMsg.setVisibility(View.GONE);
        }
    }

    private void cargarTodosLosDatos() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                originalList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Salida salida = dataSnapshot.getValue(Salida.class);
                    if (salida != null) {
                        list.add(salida);
                        originalList.add(salida);
                    }

                }
                actualizarContadorRegistros();
                historialSalidaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistorialSalidaActivity.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
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
                            Salida salida = dataSnapshot.getValue(Salida.class);
                            if (salida != null) {
                                list.add(salida);
                                originalList.add(salida);
                            }
                        }
                        actualizarContadorRegistros();
                        historialSalidaAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HistorialSalidaActivity.this, "Error al obtener datos específicos", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void actualizarContadorRegistros() {
        tvRegistroCount.setText("Total de registros: " + list.size());
        tvRegistroCount.setVisibility(View.VISIBLE);
    }
}

