package com.josedlpozo.fragments;

/**
 * Created by josedlpozo on 30/5/15.
 */

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
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
    private TextView temperatura;
    private TextView temp_dec;
    private TextView plugged;
    private TextView usb_ac;
    private TextView carga;
    private TextView health;
    private TextView volt;
    private TextView volt_dec;
    private CardView card;

    private ObservableScrollView mScrollView;

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

        card = (CardView) view.findViewById(R.id.bateria);
        temperatura = (TextView) view.findViewById(R.id.temp);
        temp_dec = (TextView) view.findViewById(R.id.temp_dec);
        plugged = (TextView) view.findViewById(R.id.plugged_ans);
        usb_ac = (TextView) view.findViewById(R.id.usb_ac);
        carga = (TextView) view.findViewById(R.id.carga);
        health = (TextView) view.findViewById(R.id.health_ans);
        volt = (TextView) view.findViewById(R.id.volt);
        volt_dec = (TextView) view.findViewById(R.id.volt_dec);
        //texto = (TextView) view.findViewById(R.id.plugged);
        img = (ImageView) view.findViewById(R.id.img_batt);
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        final Intent batteryStatus = getActivity().getApplicationContext().registerReceiver(null, ifilter);
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


        usb_ac.setText("NO");


        if (isCharging) {
            plugged.setText("SÍ");
        } else {
            plugged.setText("NO");
        }


        if (usbCharge) {
            usb_ac.setText("USB!");
        }
        if (acCharge) {
            usb_ac.setText("AC!");
        }

        icon = String.valueOf(batteryStatus.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, -1));
        BatteryManager bm = new BatteryManager();
        int bat = 0;
        int ma = 0;
        int avg = 0;


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

        }
        else {
            bat = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            ma = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            avg = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);


        }
        float batteryPct = level / (float) scale;
        int id = getResources().getIdentifier("com.josedlpozo.optimiza:drawable/" + icon, null, null);
        //img.setImageResource(id);
        //texto.setText("Is charging? " + isCharging + " -- Usb charge? " + usbCharge + "\n -- AC charge? " + acCharge + " -- Level? " + level + "\n --  " +
        //        "CA? " + bat + " -- MA? " + ma + " -- AVG? " + avg + " -- icon? " + icon + " -- temperature? " + temperature);
        //texto.setText("\n \n" + texto.getText() + " " + getInfo() + " JAJA -- " + readUsage());

        Log.i(TAG, "Is charging " + isCharging);
        Log.i(TAG, "Usb charge " + usbCharge);
        Log.i(TAG, "AC charge " + acCharge);
        Log.i(TAG, "Level " + batteryPct);
        Log.i(TAG, "CA " + bat);
        Log.i(TAG, "MA " + ma);
        Log.i(TAG, "AVG " + avg);

        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = getActivity().getApplicationContext().registerReceiver(null, ifilter);
                float temp = ((float) batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
                float voltage = ((float) batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000);
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int mHealth = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
                switch (mHealth) {
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        health.setText("BUENA");
                        break;
                    case BatteryManager.BATTERY_HEALTH_COLD:
                        health.setText("FRÍA");
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        health.setText("MUERTA");
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        health.setText("SOBREVOLTAJE");
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        health.setText("DESCONOCIDO");
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                        health.setText("FALLO DESCONOCIDO");
                        break;
                    default:
                        health.setText("NO HAY BATERIA!!");
                }
                int punto = 0;
                for (int i = 0; i < String.valueOf(voltage).length(); i++) {
                    if (String.valueOf(voltage).charAt(i) == '.') punto = i;
                }
                volt.setText(String.valueOf(voltage).substring(0, punto));
                volt_dec.setText(String.valueOf(voltage).substring(punto, String.valueOf(voltage).length() - 1));
                temperatura.setText("" + String.valueOf(temp).substring(0, 2));
                temp_dec.setText("" + String.valueOf(temp).substring(2, String.valueOf(temp).length()));
                carga.setText("" + level);
                handler.postDelayed(this, 5000);
            }
        };

        handler.postDelayed(r, 1000);

        mScrollView = (ObservableScrollView) view.findViewById(R.id.scrollView);

        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);
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