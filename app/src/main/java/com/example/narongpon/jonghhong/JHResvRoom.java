package com.example.narongpon.jonghhong;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class JHResvRoom extends Fragment {

    View rootVieW;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootVieW = inflater.inflate(R.layout.jh_resvroom, container , false);
        return rootVieW;
    }
}
