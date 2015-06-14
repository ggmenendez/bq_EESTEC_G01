package com.josedlpozo.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.josedlpozo.optimiza.R;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Created by josedlpozo on 12/6/15.
 */
public class MemoryFragment extends Fragment {

    private static final String TAG = "MEMORY";

    private ArcProgress arc;
    private ArcProgress arc2;
    private ArcProgress arc3;

    private TextView ram1;
    private TextView ram2;

    private TextView interna1;
    private TextView interna2;

    private TextView externa1;
    private TextView externa2;

    private ObservableScrollView mScrollView;

    private CardView externa_card;

    private boolean externa = false;

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
        arc2 = (ArcProgress) view.findViewById(R.id.arc_progress2);
        arc3 = (ArcProgress) view.findViewById(R.id.arc_progress3);

        externa_card = (CardView) view.findViewById(R.id.externa);

        ram1 = (TextView) view.findViewById(R.id.ram1);
        ram2 = (TextView) view.findViewById(R.id.ram2);

        interna1 = (TextView) view.findViewById(R.id.interna1);
        interna2 = (TextView) view.findViewById(R.id.interna2);

        externa1 = (TextView) view.findViewById(R.id.externa1);
        externa2 = (TextView) view.findViewById(R.id.externa2);

        externa = externalMemoryAvailable();

        if (!externa) externa_card.setVisibility(View.INVISIBLE);

        //arc.setProgress(100);
        //arc.setBottomText("JOSE");
        Log.d(TAG, "INT Av" + getAvailableInternalMemorySize());
        Log.d(TAG, "EXT Av" + getAvailableExternalMemorySize());
        Log.d(TAG, "INT T" + getTotalInternalMemorySize());
        Log.d(TAG, "EXT T" + getTotalExternalMemorySize());

        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long availMem = memoryInfo.availMem / (1024 * 1024);
        long totalMem = 0;
        String total = null;
        if (Build.VERSION.SDK_INT >= 17) {
            totalMem = memoryInfo.totalMem / (1024 * 1024);
        } else {
            total = getTotalRAM();
        }
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        if (total == null) {
            arc.setProgress((int) ((((float) availMem / (float) totalMem)) * 100));
            ram1.setText("" + twoDecimalForm.format(availMem));
            ram2.setText("/" + twoDecimalForm.format(totalMem));
        } else {
            float totalMemf = Float.parseFloat(total.replace(',', '.'));
            arc.setProgress((int) ((((float) availMem / (float) totalMemf)) * 100));
            ram1.setText("" + twoDecimalForm.format(availMem));
            ram2.setText("/" + twoDecimalForm.format(totalMemf));
        }

        interna1.setText(getAvailableInternalMemorySize().replace(",", ""));
        interna2.setText("/" + getTotalInternalMemorySize().replace(",", ""));
        arc2.setProgress((int) (Float.parseFloat(getAvailableInternalMemorySize().replace(',', '.')) / Float.parseFloat(getTotalInternalMemorySize().replace(',', '.')) * 100));
        if (externa) {
            externa1.setText(getAvailableExternalMemorySize().replace(",", ""));
            externa2.setText("/" + getTotalExternalMemorySize().replace(",", ""));
            arc3.setProgress((int) (Float.parseFloat(getAvailableExternalMemorySize().replace(',', '.')) / Float.parseFloat(getTotalExternalMemorySize().replace(',', '.')) * 100));
        }
        mScrollView = (ObservableScrollView) view.findViewById(R.id.scrollView);

        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);
    }

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize);
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return formatSize(totalBlocks * blockSize);
    }

    public static String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize);
        } else {
            return "ERROR";
        }
    }

    public static String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return formatSize(totalBlocks * blockSize);
        } else {
            return "ERROR";
        }
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        //if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    public String getTotalRAM() {

        RandomAccessFile reader = null;
        String load = null;
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        double totRam = 0;
        String lastValue = "";
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();

            // Get the Number value from the string
            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(load);
            String value = "";
            while (m.find()) {
                value = m.group(1);
                // System.out.println("Ram : " + value);
            }
            reader.close();

            totRam = Double.parseDouble(value);
            // totRam = totRam / 1024;

            double mb = totRam / 1024.0;
            double gb = totRam / 1048576.0;
            double tb = totRam / 1073741824.0;

            if (tb > 1) {
                lastValue = twoDecimalForm.format(tb);
            } else if (gb > 1) {
                lastValue = twoDecimalForm.format(gb);
            } else if (mb > 1) {
                lastValue = twoDecimalForm.format(mb);
            } else {
                lastValue = twoDecimalForm.format(totRam);
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Streams.close(reader);
        }

        return lastValue;
    }

}