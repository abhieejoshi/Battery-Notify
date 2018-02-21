package com.example.abhinav.lockscreenactivity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class CallFragment extends Fragment {

    TextView tvBatteryLevel;
    Button btnSet,btnSms;
    EditText etBattery,etNum;
    String number;
    int batteryUsr;
    String flag;
    static boolean flagSms,flagCall;
    String toastFlag;
    ImageView contactImageView;
    SharedPreferences sharedPreferences;
    final int REQUEST_CODE=123,PICK_CONTACT=2;




    public CallFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call,null);
        contactImageView = view.findViewById(R.id.imageViewContact);
        btnSms = view.findViewById(R.id.btnSetMsg);
        tvBatteryLevel = view.findViewById(R.id.textViewNattery);
        etBattery = view.findViewById(R.id.editTextBattery);
        etNum = view.findViewById(R.id.editTextNum);
        btnSet = view.findViewById(R.id.buttonSet);
        flagCall=false;
        flagSms=false;

        etNum.setSelected(false);
        etBattery.setSelected(false);

        etNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        etBattery.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hasPermission=
                        ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.SEND_SMS);

                if(hasPermission!= PackageManager.PERMISSION_GRANTED)
                {
                    if( !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.SEND_SMS))
                    {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.SEND_SMS},
                                REQUEST_CODE);
                        return;
                    }
                }
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.SEND_SMS},
                        REQUEST_CODE);

                flag = "sms";
                toastFlag="sms";
                Log.d("flagcallinsms", flagCall+"");
                flagSms = true;
                sendData();
            }
        });



        contactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hasPermission =
                        ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.READ_CONTACTS);

                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
//                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                            Manifest.permission.READ_CONTACTS)) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_CONTACTS},
                                111);
                        return;
//                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);
                }
            }
        });


        sharedPreferences = this.getActivity().getSharedPreferences("my preferences",
                MODE_PRIVATE);


        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hasPermission=
                        ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.CALL_PHONE);

                if(hasPermission!= PackageManager.PERMISSION_GRANTED)
                {
                    if( !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.CALL_PHONE))
                    {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CALL_PHONE},
                                REQUEST_CODE);
                        return;
                    }
                }
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CALL_PHONE},
                        REQUEST_CODE);

                flag = "call";
                toastFlag = "call";
                flagCall = true;
                sendData();
            }
        });

        return view;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                Cursor cursor = getActivity().getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();
                String hasPhone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                if (hasPhone.equals("1")) {
                    Cursor phones = getActivity().getContentResolver().query
                            (ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                            + " = " + contactId, null, null);
                    while (phones.moveToNext()) {
                        number = phones.getString(phones.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[-() ]", "");
                        etNum.setText(number);
                    }
                    phones.close();
                    //Do something with number
                } else {
                    etNum.setText("");
                    Toast.makeText(getActivity(), "This contact has no phone number", Toast.LENGTH_LONG).show();
                }
                cursor.close();
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private void sendData()
    {
        number = etNum.getText().toString();
        if(!(etBattery.getText().toString().equals("")) && !(number.equals(""))) {
            if (number.length() >= 10) {
                batteryUsr = Integer.valueOf(etBattery.getText().toString());
                if (batteryUsr >= 5 && batteryUsr <= 100) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("number", etNum.getText().toString());
                    editor.putInt("battery", Integer.valueOf(etBattery.getText().toString()));
                    editor.putBoolean("flagCall",flagCall);
                    editor.putBoolean("flagSms",flagSms);
                    editor.commit();

                    if(toastFlag.equals("call")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Are you sure, you want to schedule call on "+number+" at "
                                +batteryUsr+"% battery?");
                        builder.setTitle(R.string.Dialog_Call_Title);
                        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getActivity(), BatteryStatusService.class);
                                intent.putExtra("number", etNum.getText().toString());
                                intent.putExtra("flagCall", flagCall);
                                intent.putExtra("flagSms", flagSms);
                                intent.putExtra("battery", Integer.valueOf(etBattery.getText().toString()));
                                Toast.makeText(getActivity(),"Call Scheduled!",Toast.LENGTH_SHORT).show();
                                getActivity().startService(intent);

                            }
                        });
                        builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                etNum.requestFocus();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else if(toastFlag.equals("sms")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Are you sure, you want to schedule SMS on "+number+" at "
                                +batteryUsr+"% battery?");
                        builder.setTitle(R.string.Dialog_Call_Title);
                        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getActivity(), BatteryStatusService.class);
                                intent.putExtra("number", etNum.getText().toString());
                                intent.putExtra("flagCall", flagCall);
                                intent.putExtra("flagSms",flagSms);
                                intent.putExtra("battery", Integer.valueOf(etBattery.getText().toString()));
                                Toast.makeText(getActivity(),"SMS Scheduled!",Toast.LENGTH_SHORT).show();
                                getActivity().startService(intent);

                            }
                        });
                        builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                etNum.requestFocus();
                            }
                        });

                        final AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                } else {
                    etBattery.setError("Enter value between 5 to 100");

                }
            } else {
                etNum.setError("Enter Atleast 10 Digit Number!");
            }
        }
        else {
            etBattery.setError("Enter Values!");
            etNum.setError("Enter Values!");

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

