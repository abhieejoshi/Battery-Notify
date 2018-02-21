package com.example.abhinav.lockscreenactivity;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;


public class BatteryStatusService extends Service {
    int batteryLevel;
    int batteryUsr;
    String number;
    static String flagCheck;
    int counter=0;
    static Boolean flagCall = false,flagSms = false;
    Boolean flag = false;
    BatteryStateReceiver batteryStateReceiver;

    public class BatteryStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent battryStats = context.registerReceiver(null,ifilter);

//            Toast.makeText(context,"Counter:"+counter,Toast.LENGTH_SHORT).show();
            batteryLevel = battryStats.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            if(counter==0) {
//                Toast.makeText(context, "Level : " + batteryLevel, Toast.LENGTH_SHORT).show();
                if (batteryLevel == batteryUsr) {

                    if(flagCall) {
                        Intent intentCall = new Intent(Intent.ACTION_CALL);
                        intentCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intentCall.setData(Uri.parse("tel:" + number));

                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        flagCall = false;
                        CallFragment.flagCall=false;

                        context.startActivity(intentCall);
                    }

                    if(flagSms) {
                        Log.d("sms", "is in sms");

                        SharedPreferences sharedPreferences = getSharedPreferences("my preferences",
                                MODE_PRIVATE);
                         String message = sharedPreferences.getString("message","");


                         if(message.equals("")) {

                             SmsManager smsManager = SmsManager.getDefault();
//                        Toast.makeText(context,"sms sent",Toast.LENGTH_SHORT).show();
                             smsManager.sendTextMessage(number, null, "My Phone's Battey Level is: "
                                     + batteryLevel+"%", null, null);
                             flagSms = false;
                             CallFragment.flagSms=false;
                         } else {
                             SmsManager smsManager = SmsManager.getDefault();
//                        Toast.makeText(context,"sms sent",Toast.LENGTH_SHORT).show();
                             smsManager.sendTextMessage(number, null,message, null, null);
                             flagSms = false;
                             CallFragment.flagSms=false;
                         }

                    }
                        counter++;
                        flag = true;
                        stopSelf();
                }
            }
        }

    };
    public BatteryStatusService() {
    }

    @Override
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!(intent== null)) {
            batteryUsr = intent.getIntExtra("battery", 0);
            number = intent.getStringExtra("number");
            flagCall = intent.getBooleanExtra("flagCall",false);
            flagSms = intent.getBooleanExtra("flagSms",false);
            Log.d("flagcallinservice", flagCall.toString());
            Log.d("flagsmsinservice", flagSms.toString());

        }
        else
        {
            SharedPreferences sharedPreferences = getSharedPreferences("my preferences", MODE_PRIVATE);
            number = sharedPreferences.getString("number","");
            batteryUsr = sharedPreferences.getInt("battery",0);
           // flagCheck = sharedPreferences.getString("flag","");
            flagCall = sharedPreferences.getBoolean("flagCall",false);
            flagSms = sharedPreferences.getBoolean("flagSms", false);
//            if(flagCheck.equals("call")) {
//                flagCall = true;
//            }
//            else if(flagCheck.equals("sms")) {
//                flagSms = true;

//            }
        }
            return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter ifilter = new IntentFilter();
//        ifilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        ifilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        batteryStateReceiver = new BatteryStateReceiver();
        registerReceiver(batteryStateReceiver,ifilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(BatteryStatusService.this, BatteryStatusService.class);
        intent.putExtra("number", number);
        intent.putExtra("battery", batteryUsr);
        if(flagCall) {
            intent.putExtra("flagCall",true);
        }
        if(flagSms) {
            intent.putExtra("flagSms",true);
        }
       // intent.putExtra("flag", flagCheck);

        if(!(flagSms) && !(flagCall)) {
            unregisterReceiver(batteryStateReceiver);
            SharedPreferences sharedPreferences = getSharedPreferences("my preferences", MODE_PRIVATE);
            sharedPreferences.edit().remove("number");
            sharedPreferences.edit().remove("battery");
            sharedPreferences.edit().remove("flag");
            sharedPreferences.edit().commit();
            flag = true;
            stopSelf();
        }
        else {
            if (!flag) {
                startService(intent);
            }
            else {
                unregisterReceiver(batteryStateReceiver);
                SharedPreferences sharedPreferences = getSharedPreferences("my preferences", MODE_PRIVATE);
                sharedPreferences.edit().remove("number");
                sharedPreferences.edit().remove("battery");
                sharedPreferences.edit().remove("flag");
                sharedPreferences.edit().commit();
                stopSelf();
            }

        }
    }
}
