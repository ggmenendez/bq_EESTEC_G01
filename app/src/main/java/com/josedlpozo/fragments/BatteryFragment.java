package com.josedlpozo.fragments;

/**
 * Created by josedlpozo on 30/5/15.
 */

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.josedlpozo.optimiza.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;


public class BatteryFragment extends Fragment {

    private static final String TAG = "BATTERY";

    private TextView texto;
    private ImageView img;

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
        img = (ImageView) view.findViewById(R.id.img_batt);
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
        int temperature = 0;
        String icon = "";
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float temp = ((float) batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
        Toast.makeText(getActivity(), String.valueOf(temp) + "*C", Toast.LENGTH_LONG).show();
        icon = String.valueOf(batteryStatus.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, -1));
        BatteryManager bm = new BatteryManager();
        int bat = 0;
        int ma = 0;
        int avg = 0;


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            Toast.makeText(getActivity(), "NO TIENES LOLLIPOP", Toast.LENGTH_LONG).show();
        else {
            bat = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            ma = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            avg = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);


        }
        float batteryPct = level / (float) scale;
        img.setImageDrawable(Drawable.createFromPath(icon));
        texto.setText("Is charging? " + isCharging + " -- Usb charge? " + usbCharge + "\n -- AC charge? " + acCharge + " -- Level? " + level + "\n --  " +
                "CA? " + bat + " -- MA? " + ma + " -- AVG? " + avg + " -- icon? " + icon + " -- temperature? " + temperature);
        texto.setText("\n \n" + texto.getText() + " " + getInfo() + " JAJA -- " + readUsage());

        Log.i(TAG, "Is charging " + isCharging);
        Log.i(TAG, "Usb charge " + usbCharge);
        Log.i(TAG, "AC charge " + acCharge);
        Log.i(TAG, "Level " + batteryPct);
        Log.i(TAG, "CA " + bat);
        Log.i(TAG, "MA " + ma);
        Log.i(TAG, "AVG " + avg);
    }

    private String getInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append("abi: ").append(Build.CPU_ABI).append("\n");
        if (new File("/proc/cpuinfo").exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File("/proc/cpuinfo")));
                String aLine;
                while ((aLine = br.readLine()) != null) {
                    sb.append(aLine + "\n");
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private float readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" +");  // Split on one or more spaces

            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {
            }

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }
}