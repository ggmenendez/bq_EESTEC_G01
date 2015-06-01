package com.josedlpozo.fragments;

/**
 * Created by josedlpozo on 30/5/15.
 */

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.josedlpozo.optimiza.R;


public class BatteryFragment extends Fragment {

    private static final String TAG = "BATTERY";

    private TextView texto;

    public static BatteryFragment newInstance() {
        return new BatteryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_battery, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        texto = (TextView) view.findViewById(R.id.plugged);
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getActivity().getApplicationContext().registerReceiver(null, ifilter);
        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        BatteryManager bm = new BatteryManager();
        int bat = 0;
        int ma = 0;
        int avg = 0;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return;
        else {
            bat = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            ma = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            avg = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
        }
        float batteryPct = level / (float) scale;

        texto.setText("Is charging? " + isCharging + " -- Usb charge? " + usbCharge + " -- AC charge? " + acCharge);

        Log.i(TAG, "Is charging " + isCharging);
        Log.i(TAG, "Usb charge " + usbCharge);
        Log.i(TAG, "AC charge " + acCharge);
        Log.i(TAG, "Level " + batteryPct);
        Log.i(TAG, "CA " + bat);
        Log.i(TAG, "MA " + ma);
        Log.i(TAG, "AVG " + avg);
    }
}