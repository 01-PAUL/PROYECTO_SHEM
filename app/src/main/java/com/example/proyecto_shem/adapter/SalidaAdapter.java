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
import com.example.proyecto_shem.entity.Ingreso;
import com.example.proyecto_shem.entity.Salida;
import com.example.proyecto_shem.vista.DetalleActivity;
import com.example.proyecto_shem.vista.DetalleSalidaActivity;

import java.util.ArrayList;

public class SalidaAdapter extends RecyclerView.Adapter<SalidaAdapter.MyViewHolder>{

    Context context;
    ArrayList<Salida> list;

    public SalidaAdapter(Context context, ArrayList<Salida> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public SalidaAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.lista,parent,false);
        return new SalidaAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SalidaAdapter.MyViewHolder holder, int position) {
        holder.lista.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));

        Salida salida = list.get(position);
        Glide.with(context).load(salida.getImageUrl()).into(holder.ImaImageUrl);
        holder.txtUsuario.setText(salida.getUsuario());
        holder.txtCodigoUsuario.setText(salida.getCodigoUsuario());
        holder.txtTipoUsuario.setText(salida.getTipoUsuario());

        holder.lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetalleSalidaActivity.class);
                intent.putExtra("tipoUsuario", salida.getTipoUsuario());
                intent.putExtra("imageUrl", salida.getImageUrl());
                intent.putExtra("usuario", salida.getUsuario());
                intent.putExtra("codigoUsuario", salida.getCodigoUsuario());
                intent.putExtra("tipoDocumento", salida.getTipoDocumento());
                intent.putExtra("numeroDocumento", salida.getNumeroDocumento());
                intent.putExtra("micromovilidad", salida.getMicromovilidad());
                intent.putExtra("fechaSalida", salida.getFechaSalida());
                intent.putExtra("horaSalida", salida.getHoraSalida());

                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ImaImageUrl;
        TextView txtUsuario, txtCodigoUsuario, txtTipoUsuario;
        CardView lista;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ImaImageUrl = itemView.findViewById(R.id.imageUrl);
            txtUsuario = itemView.findViewById(R.id.usuario);
            txtCodigoUsuario = itemView.findViewById(R.id.codigoUsuario);
            txtTipoUsuario = itemView.findViewById(R.id.tipoUsuario);
            lista = itemView.findViewById(R.id.lista);
        }
    }
}