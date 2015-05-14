package com.example.narongpon.jonghhong;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ButtonRectangle;

public class JHChangePassword extends Fragment {
    View rootView;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private static final String TAG = "Jonghhong";
    private String oldPassword;
    private EditText edt_old, edt_new, edt_conNew;
    private ButtonRectangle btnSave;
    MaterialDialog.Builder mtr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.jh_changepassword, container, false);

        sp = getActivity().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        editor = sp.edit();
        oldPassword = sp.getString("myPassword", "");

        initWidget();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("oldpass",oldPassword);
                Log.e("old",edt_old.toString());
                mtr = new MaterialDialog.Builder(getActivity());
                mtr.title("ข้อผิดพลาด");
                if(!oldPassword.equals(edt_old.toString())) {
                    mtr.content("กรุณากรอกรหัสผ่านเดิมให้ถูกต้อง");
                    mtr.negativeText("ปิด");
                } else if (!edt_new.toString().equals(edt_conNew.toString())) {
                    mtr.content("กรุณากรอกรหัสผ่านใหม่ให้ตรงกัน");
                    mtr.negativeText("ปิด");
                } else {

                }

                mtr.show();
            }
        });

        return rootView;
    }

    public void initWidget() {
        edt_old = (EditText)rootView.findViewById(R.id.edt_oldPassword);
        edt_new = (EditText)rootView.findViewById(R.id.edt_newPassword);
        edt_conNew = (EditText)rootView.findViewById(R.id.edt_confirm);
        btnSave = (ButtonRectangle)rootView.findViewById(R.id.btn_changePassword);
    }
}
