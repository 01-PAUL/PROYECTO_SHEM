package com.example.proyecto_shem.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_shem.R;
import com.example.proyecto_shem.entity.Salida;
import com.example.proyecto_shem.vista.DetalleSalidaActivity;

import java.util.ArrayList;

public class HistorialSalidaAdapter extends RecyclerView.Adapter<HistorialSalidaAdapter.MyViewHolder>{
    Context context;
    ArrayList<Salida> list;

    public HistorialSalidaAdapter(Context context, ArrayList<Salida> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public HistorialSalidaAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.historial_lista_salida,parent,false);
        return new HistorialSalidaAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialSalidaAdapter.MyViewHolder holder, int position) {
        holder.lista.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));

        Salida salida = list.get(position);
        Glide.with(context).load(salida.getImageUrl()).into(holder.ImaImageUrl);
        holder.txtCodigoUsuario.setText(salida.getCodigoUsuario());
        holder.txtTipoMicromovilidad.setText(salida.getMicromovilidad());
        holder.txtFechaSalida.setText(salida.getFechaSalida());
        holder.txtHoraSalida.setText(salida.getHoraSalida());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ImaImageUrl;
        TextView txtCodigoUsuario, txtTipoMicromovilidad, txtFechaSalida, txtHoraSalida;
        CardView lista;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ImaImageUrl = itemView.findViewById(R.id.imageUrl);
            txtCodigoUsuario = itemView.findViewById(R.id.codigoUsuario);
            txtTipoMicromovilidad = itemView.findViewById(R.id.tipoMicromovilidad);
            txtHoraSalida = itemView.findViewById(R.id.hora);
            txtFechaSalida = itemView.findViewById(R.id.fecha);
            lista = itemView.findViewById(R.id.lista);
        }
    }
}
