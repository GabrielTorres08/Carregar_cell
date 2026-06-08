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
    private BatteryLevelReceiver batteryReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Notification notification =
                new NotificationCompat.Builder(
                        this,
                        "charging_channel")
                        .setContentTitle("Monitoramento ativo")
                        .setContentText("Monitorando bateria e carregador")
                        .setSmallIcon(
                                android.R.drawable.ic_lock_idle_charging)
                        .build();

        startForeground(1, notification);

        // Receiver do carregador
        receiver = new ChargingReceiver();

        registerReceiver(
                receiver,
                new IntentFilter(
                        Intent.ACTION_POWER_CONNECTED
                )
        );

        // Receiver da bateria
        batteryReceiver =
                new BatteryLevelReceiver();

        registerReceiver(
                batteryReceiver,
                new IntentFilter(
                        Intent.ACTION_BATTERY_CHANGED
                )
        );
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

        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}