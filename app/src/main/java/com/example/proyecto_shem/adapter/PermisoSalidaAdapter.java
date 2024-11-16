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
import com.example.proyecto_shem.entity.PermisoSalida;
import com.example.proyecto_shem.vista.DetallePermisoSalidaActivity;

import java.util.ArrayList;

public class PermisoSalidaAdapter extends RecyclerView.Adapter<PermisoSalidaAdapter.MyViewHolder> {

    Context context;
    ArrayList<PermisoSalida> list;

    public PermisoSalidaAdapter(Context context, ArrayList<PermisoSalida> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout de lista_permiso.xml
        View v = LayoutInflater.from(context).inflate(R.layout.lista_permiso, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.cardView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));

        // Obtener el permiso correspondiente a la posición
        PermisoSalida permiso = list.get(position);

        // Cargar los datos del permiso en las vistas
        Glide.with(context).load(permiso.getImageUrl()).into(holder.ImaImageUrl);
        holder.txtUsuario.setText(permiso.getUsuario());
        //add
        holder.txtArea.setText(permiso.getArea());
        holder.txtMotivo.setText(permiso.getDetallePermiso());

        // Configurar el click para ver los detalles del permiso
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetallePermisoSalidaActivity.class);
            // Pasar todos los datos necesarios a la actividad de detalles
            intent.putExtra("imageUrl", permiso.getImageUrl());
            intent.putExtra("usuario", permiso.getUsuario());
            intent.putExtra("motivo", permiso.getDetallePermiso());
            intent.putExtra("micromovilidad", permiso.getMicromovilidad());
            intent.putExtra("area", permiso.getArea());
            intent.putExtra("fechaSalida", permiso.getFechaSalida());
            intent.putExtra("horaSalida", permiso.getHoraSalida());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ImaImageUrl;
        TextView txtUsuario, txtArea,  txtMotivo;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Vincular las vistas con los elementos del layout
            ImaImageUrl = itemView.findViewById(R.id.imageUrl);
            txtUsuario = itemView.findViewById(R.id.usuario);
            //add
            txtArea = itemView.findViewById(R.id.area);
            txtMotivo = itemView.findViewById(R.id.motivo);
            cardView = itemView.findViewById(R.id.lista_permiso);
        }
    }






}