package com.example.proyecto_shem.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_shem.R;
import com.example.proyecto_shem.entity.Ingreso;
import com.example.proyecto_shem.vista.DetalleActivity;

import java.util.ArrayList;
import java.util.List;

public class IngresoAdapter extends RecyclerView.Adapter<IngresoAdapter.MyViewHolder> {

    Context context;
    ArrayList<Ingreso> list;

    public IngresoAdapter(Context context, ArrayList<Ingreso> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.lista,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.lista.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));

        Ingreso ingreso = list.get(position);
        Glide.with(context).load(ingreso.getImageUrl()).into(holder.ImaImageUrl);
        holder.txtUsuario.setText(ingreso.getUsuario());
        holder.txtCodigoUsuario.setText(ingreso.getCodigoUsuario());
        holder.txtTipoUsuario.setText(ingreso.getTipoUsuario());

        holder.lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetalleActivity.class);
                intent.putExtra("tipoUsuario", ingreso.getTipoUsuario());
                intent.putExtra("imageUrl", ingreso.getImageUrl());
                intent.putExtra("usuario", ingreso.getUsuario());
                intent.putExtra("codigoUsuario", ingreso.getCodigoUsuario());
                intent.putExtra("tipoDocumento", ingreso.getTipoDocumento());
                intent.putExtra("numeroDocumento", ingreso.getNumeroDocumento());
                intent.putExtra("tipoMicromovilidad", ingreso.getTipoMicromovilidad());
                intent.putExtra("fechaIngreso", ingreso.getFechaIngreso());
                intent.putExtra("horaIngreso", ingreso.getHoraIngreso());

                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
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