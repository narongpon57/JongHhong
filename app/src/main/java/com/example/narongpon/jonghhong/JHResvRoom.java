package com.example.narongpon.jonghhong;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.gc.materialdesign.views.ButtonRectangle;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.TimeZone;

public class JHResvRoom extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    View rootVieW;

    private EditText edtResvDate, edtStTime, edtEnTime;
    private ButtonRectangle btnSave;
    private String chkTime = "";

    private DatePickerDialog mDatePicker;
    private TimePickerDialog mTimePicker;
    private Calendar mCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Bangkok"));


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootVieW = inflater.inflate(R.layout.jh_resvroom, container , false);

        initWidget();


        mDatePicker = DatePickerDialog.newInstance(
                this,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH),
                false);

        mTimePicker = TimePickerDialog.newInstance(
                this,
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE),
                true,
                false);

        edtResvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePicker.setYearRange(2014,2034);
                mDatePicker.show(getActivity().getSupportFragmentManager(), "DatePicker");
            }
        });

        edtStTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkTime = "st";
                mTimePicker.show(getActivity().getSupportFragmentManager(), "TimePicker");
            }
        });

        edtEnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkTime = "en";
                mTimePicker.show(getActivity().getSupportFragmentManager(), "TimePicker");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resvRoom();
            }
        });

        return rootVieW;
    }

    public void initWidget() {
        edtResvDate = (EditText)rootVieW.findViewById(R.id.resvRoom_edt);
        edtStTime = (EditText)rootVieW.findViewById(R.id.stTime_edt);
        edtEnTime = (EditText)rootVieW.findViewById(R.id.enTime_edt);
        btnSave = (ButtonRectangle)rootVieW.findViewById(R.id.btn_resvRoom);
    }

    public void resvRoom() {

    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

        int currentMonth = month + 1;

        edtResvDate.setText(day + "/" + currentMonth + "/" + year);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view,int hourOfDay, int minute) {

        if(chkTime.equals("st")) {
            edtStTime.setText(hourOfDay + "." + minute);
        } else if(chkTime.equals("en")) {
            edtEnTime.setText(hourOfDay + "." + minute);
        }

    }
}
