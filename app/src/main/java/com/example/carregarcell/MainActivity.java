package com.example.carregarcell;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_CHARGER_AUDIO = 100;
    private static final int PICK_BATTERY_AUDIO = 200;

    private boolean escolhendoAudioBateria = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCarregador =
                findViewById(R.id.btnSelecionarAudio);

        Button btnBateria =
                findViewById(R.id.btnSelecionarAudioBateria);

        Button btnSalvar =
                findViewById(R.id.btnSalvarPorcentagem);

        EditText edtPorcentagem =
                findViewById(R.id.edtPorcentagem);

        SharedPreferences prefs =
                getSharedPreferences(
                        "config",
                        MODE_PRIVATE
                );

        edtPorcentagem.setText(
                String.valueOf(
                        prefs.getInt(
                                "battery_limit",
                                95
                        )
                )
        );

        btnCarregador.setOnClickListener(v -> {

            escolhendoAudioBateria = false;

            Intent intent =
                    new Intent(
                            Intent.ACTION_OPEN_DOCUMENT
                    );

            intent.setType("audio/*");

            startActivityForResult(
                    intent,
                    PICK_CHARGER_AUDIO
            );
        });

        btnBateria.setOnClickListener(v -> {

            escolhendoAudioBateria = true;

            Intent intent =
                    new Intent(
                            Intent.ACTION_OPEN_DOCUMENT
                    );

            intent.setType("audio/*");

            startActivityForResult(
                    intent,
                    PICK_BATTERY_AUDIO
            );
        });

        btnSalvar.setOnClickListener(v -> {

            try {

                int valor =
                        Integer.parseInt(
                                edtPorcentagem
                                        .getText()
                                        .toString()
                        );

                prefs.edit()
                        .putInt(
                                "battery_limit",
                                valor
                        )
                        .apply();

                Toast.makeText(
                        this,
                        "Porcentagem salva",
                        Toast.LENGTH_SHORT
                ).show();

            } catch (Exception e) {

                Toast.makeText(
                        this,
                        "Valor inválido",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        Intent serviceIntent =
                new Intent(
                        this,
                        ChargingService.class
                );

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

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (resultCode != RESULT_OK
                || data == null) {
            return;
        }

        Uri audioUri = data.getData();

        if (audioUri == null) {
            return;
        }

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

        if (requestCode == PICK_CHARGER_AUDIO) {

            prefs.edit()
                    .putString(
                            "audio_uri",
                            audioUri.toString()
                    )
                    .apply();

            Toast.makeText(
                    this,
                    "Áudio do carregador salvo",
                    Toast.LENGTH_SHORT
            ).show();
        }

        if (requestCode == PICK_BATTERY_AUDIO) {

            prefs.edit()
                    .putString(
                            "battery_audio_uri",
                            audioUri.toString()
                    )
                    .apply();

            Toast.makeText(
                    this,
                    "Áudio da bateria salvo",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}