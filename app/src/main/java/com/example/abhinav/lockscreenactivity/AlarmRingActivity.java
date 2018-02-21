package com.example.abhinav.lockscreenactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import static android.media.AudioAttributes.USAGE_ALARM;

public class AlarmRingActivity extends AppCompatActivity {
    Button btnStopAlarm;
    MediaPlayer mediaPlayer;
    Vibrator vibrator;
    String flagChckBox;
    ChargerDisconnectReceiver chargerDisconnectReceiver;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_alarm_ring);
        btnStopAlarm = findViewById(R.id.buttonStop);

        SharedPreferences sharedPreferences = getSharedPreferences("my preferences", MODE_PRIVATE);
        flagChckBox = sharedPreferences.getString("flagUnplug","");
        if(flagChckBox.equals("true")) {

            btnStopAlarm.setEnabled(false);
            btnStopAlarm.setText("Unplug charger to Stop Alarm!");
        }


                Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                if (alarmUri == null) {
                    alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                }

            mediaPlayer = new  MediaPlayer();
            mediaPlayer.setLooping(true);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM).build();
            mediaPlayer.setAudioAttributes(audioAttributes);
        try {
            mediaPlayer.setDataSource(AlarmRingActivity.this,alarmUri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                long [] pattern = {0,1000,1000};
                vibrator.vibrate(pattern,0);
        mediaPlayer.start();



        btnStopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                vibrator.cancel();
                Toast.makeText(AlarmRingActivity.this,"Please, Unplug the Charger " +
                                "and Save Energy!",Toast.LENGTH_LONG).show();
                finish();

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        chargerDisconnectReceiver = new ChargerDisconnectReceiver();
        IntentFilter ifilter = new IntentFilter();;
        ifilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(chargerDisconnectReceiver,ifilter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(chargerDisconnectReceiver);
    }



    private class ChargerDisconnectReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED))
            {
                Log.i("power","charger disconnected");
                Toast.makeText(context,"Charger Disconnected", Toast.LENGTH_SHORT).show();
                mediaPlayer.stop();
                vibrator.cancel();
                Toast.makeText(AlarmRingActivity.this,"Please, Unplug the Charger " +
                        "and Save Energy!",Toast.LENGTH_LONG).show();
                finish();
            }

        }

    }
}
