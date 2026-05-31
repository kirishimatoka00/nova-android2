package com.kirishimatoka00.nova120;

import android.content.Intent;
import android.os.Build;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "RecordingService")
public class RecordingServicePlugin extends Plugin {

    // JS 呼叫：Capacitor.Plugins.RecordingService.startService()
    @PluginMethod
    public void startService(PluginCall call) {
        try {
            Intent intent = new Intent(getContext(), RecordingForegroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getContext().startForegroundService(intent);
            } else {
                getContext().startService(intent);
            }
            call.resolve();
        } catch (Exception e) {
            call.reject("啟動錄音服務失敗: " + e.getMessage());
        }
    }

    // JS 呼叫：Capacitor.Plugins.RecordingService.stopService()
    @PluginMethod
    public void stopService(PluginCall call) {
        try {
            Intent intent = new Intent(getContext(), RecordingForegroundService.class);
            intent.setAction(RecordingForegroundService.ACTION_STOP);
            getContext().startService(intent);
            call.resolve();
        } catch (Exception e) {
            call.reject("停止錄音服務失敗: " + e.getMessage());
        }
    }
}
