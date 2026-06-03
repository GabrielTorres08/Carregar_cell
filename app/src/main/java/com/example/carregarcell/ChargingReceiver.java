package com.example.carregarcell;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;

public class ChargingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())) {
            return;
        }

        try {

            SharedPreferences prefs =
                    context.getSharedPreferences(
                            "config",
                            Context.MODE_PRIVATE
                    );

            String uriString =
                    prefs.getString(
                            "audio_uri",
                            null
                    );

            if (uriString == null) {
                return;
            }

            Uri audioUri = Uri.parse(uriString);

            MediaPlayer player = new MediaPlayer();

            player.setDataSource(
                    context,
                    audioUri
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
}