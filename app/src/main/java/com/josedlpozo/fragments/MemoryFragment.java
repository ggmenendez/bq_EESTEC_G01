package com.josedlpozo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.josedlpozo.optimiza.R;


/**
 * Created by josedlpozo on 12/6/15.
 */
public class MemoryFragment extends Fragment {

    private static final String TAG = "MEMORY";

    private ArcProgress arc;


    public static MemoryFragment newInstance() {
        return new MemoryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_memory, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        arc = (ArcProgress) view.findViewById(R.id.arc_progress);

        arc.setProgress(100);
        arc.setBottomText("JOSE");
    }

}