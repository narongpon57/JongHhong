package com.example.narongpon.jonghhong;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.gc.materialdesign.views.ButtonRectangle;

public class JHCheckRoom extends Fragment{

    View rootView;
    private ButtonRectangle btnSearch;
    private EditText edt_date, edt_room;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.jh_checkroom, container, false);

        initWidget();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainDrawer mainDrawer = (MainDrawer)getActivity();
                mainDrawer.showCheckRoom();
            }
        });

        return rootView;


    }

    public void initWidget() {
        btnSearch = (ButtonRectangle)rootView.findViewById(R.id.btn_search);
        edt_date = (EditText)rootView.findViewById(R.id.date_search);
        edt_room = (EditText)rootView.findViewById(R.id.room_search);
    }
}

