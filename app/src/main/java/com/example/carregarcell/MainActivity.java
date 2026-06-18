package com.example.carregarcell;


import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_CHARGER_AUDIO = 100;
    private static final int PICK_BATTERY_AUDIO = 200;

    private TextView txtPercentual;
    private SeekBar seekBattery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs =
                getSharedPreferences(
                        "config",
                        MODE_PRIVATE
                );

        txtPercentual =
                findViewById(R.id.txtPercentual);

        seekBattery =
                findViewById(R.id.seekBattery);

        int limiteSalvo =
                prefs.getInt(
                        "battery_limit",
                        95
                );

        txtPercentual.setText(
                limiteSalvo + "%"
        );

        seekBattery.setProgress(
                limiteSalvo
        );

        seekBattery.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        txtPercentual.setText(
                                progress + "%"
                        );
                    }

                    @Override
                    public void onStartTrackingTouch(
                            SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(
                            SeekBar seekBar) {

                        prefs.edit()
                                .putInt(
                                        "battery_limit",
                                        seekBar.getProgress()
                                )
                                .apply();

                        Toast.makeText(
                                MainActivity.this,
                                "Limite salvo em "
                                        + seekBar.getProgress()
                                        + "%",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        findViewById(R.id.btnSelecionarAudio)
                .setOnClickListener(v -> {

                    Intent intent =
                            new Intent(
                                    Intent.ACTION_OPEN_DOCUMENT
                            );

                    intent.addCategory(
                            Intent.CATEGORY_OPENABLE
                    );

                    intent.setType("audio/*");

                    startActivityForResult(
                            intent,
                            PICK_CHARGER_AUDIO
                    );
                });

        findViewById(R.id.btnSelecionarAudioBateria)
                .setOnClickListener(v -> {

                    Intent intent =
                            new Intent(
                                    Intent.ACTION_OPEN_DOCUMENT
                            );

                    intent.addCategory(
                            Intent.CATEGORY_OPENABLE
                    );

                    intent.setType("audio/*");

                    startActivityForResult(
                            intent,
                            PICK_BATTERY_AUDIO
                    );
                });

        Intent serviceIntent =
                new Intent(
                        this,
                        ChargingService.class
                );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(
                    serviceIntent
            );
        } else {
            startService(
                    serviceIntent
            );
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

        Uri audioUri =
                data.getData();

        if (audioUri == null) {
            return;
        }

        try {

            getContentResolver()
                    .takePersistableUriPermission(
                            audioUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );

        } catch (Exception ignored) {
        }

        SharedPreferences prefs =
                getSharedPreferences(
                        "config",
                        MODE_PRIVATE
                );

        if (requestCode ==
                PICK_CHARGER_AUDIO) {

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

        if (requestCode ==
                PICK_BATTERY_AUDIO) {

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