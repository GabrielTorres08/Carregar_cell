package com.example.carregarcell;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_AUDIO_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSelecionar = findViewById(R.id.btnSelecionarAudio);

        btnSelecionar.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("audio/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            startActivityForResult(intent, PICK_AUDIO_REQUEST);
        });

        Intent serviceIntent =
                new Intent(this, ChargingService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO_REQUEST
                && resultCode == RESULT_OK
                && data != null) {

            Uri audioUri = data.getData();

            if (audioUri != null) {

                getContentResolver()
                        .takePersistableUriPermission(
                                audioUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );

                SharedPreferences prefs =
                        getSharedPreferences(
                                "config",
                                MODE_PRIVATE
                        );

                prefs.edit()
                        .putString(
                                "audio_uri",
                                audioUri.toString()
                        )
                        .apply();

                Toast.makeText(
                        this,
                        "Áudio salvo com sucesso!",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}