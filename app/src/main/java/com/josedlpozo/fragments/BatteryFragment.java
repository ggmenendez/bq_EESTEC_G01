package com.josedlpozo.fragments;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.josedlpozo.optimiza.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by josedlpozo on 30/5/15.
 * <p/>
 * Fragment encargado de datos de batería
 * <p/>
 * Muestra datos de temperatura de bateria, si esta cargandose o no y porque medios, nivel de carga,
 * salud de la bateria y voltaje.
 * <p/>
 * Implementa un thread para actualizar los datos cada XX segundos
 */

public class BatteryFragment extends Fragment {

    private static final String TAG = "BATTERY";

    // Elementos necesarios para representación de datos.
    private TextView temperatura;
    private TextView temp_dec;
    private TextView plugged;
    private TextView usb_ac;
    private TextView carga;
    private TextView volt;
    private TextView volt_dec;

    //ScrollView
    private ObservableScrollView mScrollView;

    // Intent para recoger datos lanzados por la bateria.
    Intent batteryStatus;

    // Handler para ejecutar la thread r
    Handler handler;

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
        temperatura = (TextView) view.findViewById(R.id.temp);
        temp_dec = (TextView) view.findViewById(R.id.temp_dec);
        plugged = (TextView) view.findViewById(R.id.plugged_ans);
        usb_ac = (TextView) view.findViewById(R.id.usb_ac);
        carga = (TextView) view.findViewById(R.id.carga);
        volt = (TextView) view.findViewById(R.id.volt);
        volt_dec = (TextView) view.findViewById(R.id.volt_dec);


        // Inicialización y periodo de ejecución
        handler = new Handler();
        handler.postDelayed(r, 1000);

        mScrollView = (ObservableScrollView) view.findViewById(R.id.scrollView);

        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);
    }

    Runnable r = new Runnable() {
        public void run() {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            batteryStatus = getActivity().getApplicationContext().registerReceiver(null, ifilter);
            float temp = ((float) batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
            float voltage = ((float) batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int mHealth = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

            //Buscamos el punto de la parte decimal para mejor visualización de datos
            int punto = 0;
            for (int i = 0; i < String.valueOf(voltage).length(); i++) {
                if (String.valueOf(voltage).charAt(i) == '.') punto = i;
            }
            usb_ac.setText("NO");


            if (isCharging) {
                plugged.setText("SÍ");
            } else {
                plugged.setText("NO");
            }


            if (usbCharge) {
                usb_ac.setText("USB");
            }
            if (acCharge) {
                usb_ac.setText("AC");
            }

            volt.setText(String.valueOf(voltage).substring(0, punto));
            volt_dec.setText(String.valueOf(voltage).substring(punto, String.valueOf(voltage).length() - 1));
            temperatura.setText("" + String.valueOf(temp).substring(0, 2));
            temp_dec.setText("" + String.valueOf(temp).substring(2, String.valueOf(temp).length()));
            carga.setText("" + level);
            handler.postDelayed(this, 5000);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(r, 5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(r);
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