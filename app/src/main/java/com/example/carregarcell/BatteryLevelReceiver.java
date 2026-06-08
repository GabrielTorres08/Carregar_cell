package com.example.carregarcell;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;

public class BatteryLevelReceiver extends BroadcastReceiver {

    private boolean jaTocou = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        int level =
                intent.getIntExtra(
                        BatteryManager.EXTRA_LEVEL,
                        -1
                );

        SharedPreferences prefs =
                context.getSharedPreferences(
                        "config",
                        Context.MODE_PRIVATE
                );

        int limite =
                prefs.getInt(
                        "battery_limit",
                        100
                );

        if (level >= limite && !jaTocou) {

            jaTocou = true;

            try {

                String uriString =
                        prefs.getString(
                                "battery_audio_uri",
                                null
                        );

                if (uriString == null)
                    return;

                Uri uri = Uri.parse(uriString);

                MediaPlayer player =
                        new MediaPlayer();

                player.setDataSource(
                        context,
                        uri
                );

                player.prepare();

                player.start();

                player.setOnCompletionListener(mp -> {
                    mp.release();
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (level < limite) {
            jaTocou = false;
        }
    }
}