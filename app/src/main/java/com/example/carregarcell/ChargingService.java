package com.example.carregarcell;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class ChargingService extends Service {

    private ChargingReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Notification notification =
                new NotificationCompat.Builder(this, "charging_channel")
                        .setContentTitle("Monitoramento ativo")
                        .setContentText("Aguardando carregador")
                        .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
                        .build();

        startForeground(1, notification);

        receiver = new ChargingReceiver();

        IntentFilter filter =
                new IntentFilter(Intent.ACTION_POWER_CONNECTED);

        registerReceiver(receiver, filter);
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            "charging_channel",
                            "Monitoramento",
                            NotificationManager.IMPORTANCE_LOW
                    );

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {

        if (receiver != null) {
            unregisterReceiver(receiver);
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}