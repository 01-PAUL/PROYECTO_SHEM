package com.example.proyecto_shem.vista;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_shem.R;

public class InicioActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_inicio);

        // Animación
        Animation animacion1 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_centro);
        ImageView logo = findViewById(R.id.logo);
        logo.setAnimation(animacion1);

        // Iniciar el audio
        mediaPlayer = MediaPlayer.create(this, R.raw.cibertec);
        mediaPlayer.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(InicioActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detener y liberar el MediaPlayer para evitar fugas de memoria
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
