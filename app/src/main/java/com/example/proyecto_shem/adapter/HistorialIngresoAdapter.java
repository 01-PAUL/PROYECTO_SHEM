package com.example.proyecto_shem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_shem.R;
import com.example.proyecto_shem.entity.Ingreso;

import java.util.ArrayList;

public class HistorialIngresoAdapter extends RecyclerView.Adapter<HistorialIngresoAdapter.MyViewHolder> {

    Context context;
    ArrayList<Ingreso> list;

    public HistorialIngresoAdapter(Context context, ArrayList<Ingreso> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public HistorialIngresoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.historial_lista_ingreso,parent,false);
        return new HistorialIngresoAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialIngresoAdapter.MyViewHolder holder, int position) {
        holder.historial.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));

        Ingreso ingreso = list.get(position);
        holder.tipoUsuario.setText(ingreso.getTipoUsuario());
        holder.movilidadHistorial.setText(ingreso.getTipoMicromovilidad());
        holder.fechaIngreso.setText(ingreso.getFechaIngreso());
        holder.horaIngreso.setText(ingreso.getHoraIngreso());
        holder.codigoUsuario.setText(ingreso.getCodigoUsuario());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tipoUsuario, movilidadHistorial, fechaIngreso, horaIngreso, codigoUsuario;
        CardView historial;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tipoUsuario = itemView.findViewById(R.id.tipoUsuario);
            movilidadHistorial = itemView.findViewById(R.id.movilidadHistorial);
            fechaIngreso = itemView.findViewById(R.id.fechaIngreso);
            horaIngreso = itemView.findViewById(R.id.horaIngreso);
            codigoUsuario = itemView.findViewById(R.id.codigoUsuario);
            historial = itemView.findViewById(R.id.historial);
        }
    }
}