package com.kirishimatoka00.nova120;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class RecordingForegroundService extends Service {

    private static final String CHANNEL_ID   = "nova_recording_channel";
    private static final String CHANNEL_NAME = "NOVA 錄音服務";
    private static final int    NOTIF_ID     = 2001;
    public  static final String ACTION_STOP  = "ACTION_STOP_RECORDING";

    // ── 建立服務時先建立 Notification Channel（Android 8+ 必要）
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // 收到 STOP 指令時關閉服務
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }

        // 點擊通知返回 App
        Intent openApp = new Intent(this, MainActivity.class);
        openApp.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT |
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                        ? PendingIntent.FLAG_IMMUTABLE : 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, openApp, pendingFlags);

        // 建立持續顯示的通知（這是 Foreground Service 的核心）
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("NOVA 120 SSP — 錄音中")
                .setContentText("點此返回 App，關閉螢幕不會中斷錄音")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true)          // 使用者無法手動滑掉
                .setSilent(true)           // 不發出提示音
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(NOTIF_ID, notification);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // 不需要綁定模式
    }

    // ── 建立 Notification Channel（Android 8.0 Oreo 以上必要）
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW  // LOW = 不震動、不發聲
            );
            channel.setDescription("錄音進行中的背景保活通知");
            channel.setSound(null, null);               // 靜音
            channel.enableVibration(false);

            NotificationManager manager =
                    (NotificationManager) getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
