package com.example.abhinav.lockscreenactivity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.zip.Inflater;


public class AlarmFragment extends Fragment {
    ImageView ivSetAlarm;



    public AlarmFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alarm,null);
        ivSetAlarm = view.findViewById(R.id.ivSetAlarm);

        ivSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Alarm set at 100% battery level!",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),AlarmService.class);
                getActivity().startService(intent);
            }
        });
        return view;

    }

}
