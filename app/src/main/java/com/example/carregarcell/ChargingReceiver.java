package com.example.carregarcell;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

public class ChargingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())) {

            MediaPlayer player =
                    MediaPlayer.create(context, R.raw.musica);

            player.start();

            player.setOnCompletionListener(mp -> {
                mp.release();
            });
        }
    }
}