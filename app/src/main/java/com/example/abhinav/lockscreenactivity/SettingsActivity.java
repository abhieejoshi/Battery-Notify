package com.example.abhinav.lockscreenactivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    Button btnDisMsg,btnDisCall,btnDisAlarm,btnSave,btnRestore;
    CheckBox checkBox;
    String flagChckBox;
    EditText etCustomMsg;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        btnDisMsg = findViewById(R.id.btnDisMsg);
        btnDisCall = findViewById(R.id.btnDisCall);
        btnDisAlarm = findViewById(R.id.btnDisAlarm);
        checkBox = findViewById(R.id.checkBox);
        etCustomMsg = findViewById(R.id.editTextMsg);
        btnSave = findViewById(R.id.buttonSave);
        btnRestore = findViewById(R.id.btnRestore);
        btnRestore.setEnabled(false);

        etCustomMsg.setSelected(false);

        sharedPreferences = getSharedPreferences("my preferences",
                MODE_PRIVATE);
        editor = sharedPreferences.edit();

        etCustomMsg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        String message = sharedPreferences.getString("message","");
        if (!message.equals("")) {
            btnRestore.setEnabled(true);
            etCustomMsg.setText(message);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(etCustomMsg.getText().toString().equals(""))) {
                    String message = etCustomMsg.getText().toString();
                    editor.putString("message",message);
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "Message Saved!", Toast.LENGTH_SHORT).show();
                }
                else {
                    etCustomMsg.setError("Enter Message");
                    etCustomMsg.requestFocus();
                }
            }
        });

        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etCustomMsg.setText("");
                etCustomMsg.setHint("Default: My Phone's Battey Level is: X %");
                editor.putString("message","");
                editor.commit();
                btnRestore.setEnabled(false);

            }
        });




       // SharedPreferences sharedPreferences = getSharedPreferences("my preferences", MODE_PRIVATE);
        flagChckBox = sharedPreferences.getString("flagUnplug","");

        if(flagChckBox.equals("true")) {
            checkBox.setChecked(true);
        }


            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkBox.isChecked()) {

                    editor.putString("flagUnplug","true");
                    editor.commit();
                }
                else {
                    editor.putString("flagUnplug","false");
                    editor.commit();
                }
            }
        });



        if(!(BatteryStatusService.flagCall)) {
            btnDisCall.setText("Schedule a call first");
            btnDisCall.setEnabled(false);
        }
        if(!(BatteryStatusService.flagSms)) {
            btnDisMsg.setText("Schedule a message first");
            btnDisMsg.setEnabled(false);
        }
        if(!(AlarmService.flagDisBtn)){
            btnDisAlarm.setText("Schedule an alarm first");
            btnDisAlarm.setEnabled(false);
        }

        btnDisAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this,AlarmService.class);
                AlarmService.flag = true;
                stopService(intent);
                Toast.makeText(SettingsActivity.this,"Scheduled Alarm is canceled!",
                        Toast.LENGTH_LONG).show();
                btnDisAlarm.setText("Schedule an alarm first");
                btnDisAlarm.setEnabled(false);
            }
        });

        btnDisCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BatteryStatusService.flagCall = false;
//                if(BatteryStatusService.flagCall)
//                {
//                    BatteryStatusService.
//                }
                Intent intent = new Intent(SettingsActivity.this,BatteryStatusService.class);
                stopService(intent);
                Toast.makeText(SettingsActivity.this,"Scheduled Call is canceled!",
                        Toast.LENGTH_LONG).show();
                btnDisCall.setText("Schedule a call first");
                btnDisCall.setEnabled(false);

            }
        });

        btnDisMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BatteryStatusService.flagSms = false;
//                if(BatteryStatusService.flagCheck.equals("sms"))
//                {
//                    BatteryStatusService.flagCheck = "";
//                }
                Intent intent = new Intent(SettingsActivity.this,BatteryStatusService.class);
                stopService(intent);
                Toast.makeText(SettingsActivity.this,"Scheduled Sms is canceled!",
                        Toast.LENGTH_LONG).show();
                btnDisMsg.setText("Schedule a message first");
                btnDisMsg.setEnabled(false);
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
