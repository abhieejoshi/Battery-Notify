package com.example.abhinav.lockscreenactivity;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class AlarmService extends Service {
    static boolean flag = false;
    static boolean flagDisBtn = false;
    BatteryFullReceiver batteryFullReceiver;

    public AlarmService() {
    }

    public class BatteryFullReceiver extends BroadcastReceiver {

        public BatteryFullReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            ifilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            Intent battryStatus = context.registerReceiver(null,ifilter);

            int status = battryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

            if(status == 100) {
//                Toast.makeText(context.getApplicationContext(),"In if body",Toast.LENGTH_SHORT).show();
                Intent intentAlarm = new Intent(context.getApplicationContext(),AlarmRingActivity.class);
                intentAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentAlarm);
                flag = true;
                stopSelf();
            }
        }
    }

    @Override
    @Nullable
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        flagDisBtn = true;
        batteryFullReceiver = new BatteryFullReceiver();
        IntentFilter ifilter = new IntentFilter();
//        ifilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        ifilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryFullReceiver,ifilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(AlarmService.this,AlarmService.class);
        if (!flag) {
            startService(intent);
        }
        else {
            flagDisBtn = false;
            unregisterReceiver(batteryFullReceiver);
            stopSelf();
        }
    }
}
