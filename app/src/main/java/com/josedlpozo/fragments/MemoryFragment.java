package com.josedlpozo.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

    // Arcos de progreso para memoria RAM, INTERNA, EXTERNA
    private ArcProgress arc;
    private ArcProgress arc2;
    private ArcProgress arc3;

    // Textos de cada memoria
    private TextView ram1;
    private TextView ram2;

    private TextView interna1;
    private TextView interna2;

    private TextView externa1;
    private TextView externa2;

    // ScrollView
    private ObservableScrollView mScrollView;

    // CardView para esconder si no tiene memoria externa el dispositivo
    private CardView externa_card;

    private boolean externa = false;

    // Handler para ejecutar la thread r
    Handler handler;

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

        //externa_card = (CardView) view.findViewById(R.id.externa);

        ram1 = (TextView) view.findViewById(R.id.ram1);
        ram2 = (TextView) view.findViewById(R.id.ram2);

        interna1 = (TextView) view.findViewById(R.id.interna1);
        interna2 = (TextView) view.findViewById(R.id.interna2);

        externa1 = (TextView) view.findViewById(R.id.externa1);
        externa2 = (TextView) view.findViewById(R.id.externa2);

        externa = externalMemoryAvailable();

        if (!externa) externa_card.setVisibility(View.INVISIBLE);

        // Inicialización y periodo de ejecución
        handler = new Handler();
        handler.postDelayed(r, 1000);

        mScrollView = (ObservableScrollView) view.findViewById(R.id.scrollView);

        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);
    }

    Runnable r = new Runnable() {
        public void run() {
            ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            long availMem = memoryInfo.availMem / (1024 * 1024);
            long totalMem = 0;
            String total = null;
            if (Build.VERSION.SDK_INT >= 16) {
                totalMem = memoryInfo.totalMem / (1024 * 1024);
            } else {
                total = getTotalRAM();
            }
            DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
            if (total == null) {
                int progress = (int) ((((totalMem - (float) availMem) / (float) totalMem)) * 100);
                if (progress <= 70) {
                    arc.setFinishedStrokeColor(getResources().getColor(R.color.green));
                    arc.setTextColor(getResources().getColor(R.color.green));
                } else if (progress > 70 && progress < 80) {
                    arc.setFinishedStrokeColor(getResources().getColor(R.color.warning));
                    arc.setTextColor(getResources().getColor(R.color.warning));
                } else {
                    arc.setFinishedStrokeColor(getResources().getColor(R.color.red));
                    arc.setTextColor(getResources().getColor(R.color.red));
                }
                arc.setProgress(progress);
                ram1.setText("" + twoDecimalForm.format(totalMem - availMem));
                ram2.setText("/" + twoDecimalForm.format(totalMem));
            } else {

                float totalMemf = Float.parseFloat(total.replace(",", ""));
                Log.d("uuu", "entra" + totalMemf);
                int progress = (int) ((((((float) totalMemf) - (float) availMem) / (float) totalMemf)) * 100);
                if (progress <= 70) {
                    arc.setFinishedStrokeColor(getResources().getColor(R.color.green));
                    arc.setTextColor(getResources().getColor(R.color.green));
                } else if (progress > 70 && progress < 80) {
                    arc.setFinishedStrokeColor(getResources().getColor(R.color.warning));
                    arc.setTextColor(getResources().getColor(R.color.warning));
                } else {
                    arc.setFinishedStrokeColor(getResources().getColor(R.color.red));
                    arc.setTextColor(getResources().getColor(R.color.red));
                }
                arc.setProgress(progress);
                ram1.setText("" + twoDecimalForm.format(totalMemf - availMem));
                ram2.setText("/" + twoDecimalForm.format(totalMemf));
            }
            if (Build.VERSION.SDK_INT > 18) {
                interna1.setText(String.valueOf((int) (Float.parseFloat(getTotalInternalMemorySize18().replace(",", "")) - Float.parseFloat(getAvailableInternalMemorySize18().replace(",", "")))));
                interna2.setText("/" + getTotalInternalMemorySize18().replace(",", ""));
                int progress = (int) ((Float.parseFloat(getTotalInternalMemorySize18().replace(",", "")) - Float.parseFloat(getAvailableInternalMemorySize18().replace(",", ""))) / Float.parseFloat(getTotalInternalMemorySize18().replace(",", "")) * 100);
                Log.d("xxx", "1" + progress);
                if (progress <= 70) {
                    arc2.setFinishedStrokeColor(getResources().getColor(R.color.green));
                    arc2.setTextColor(getResources().getColor(R.color.green));
                } else if (progress > 70 && progress < 80) {
                    arc2.setFinishedStrokeColor(getResources().getColor(R.color.warning));
                    arc2.setTextColor(getResources().getColor(R.color.warning));
                } else {
                    arc2.setFinishedStrokeColor(getResources().getColor(R.color.red));
                    arc2.setTextColor(getResources().getColor(R.color.red));
                }
                arc2.setProgress(progress);
            } else {
                interna1.setText(String.valueOf((int) (Float.parseFloat(getTotalInternalMemorySize().replace(",", "")) - Float.parseFloat(getAvailableInternalMemorySize().replace(",", "")))));
                interna2.setText("/" + getTotalInternalMemorySize().replace(",", ""));
                int progress = (int) ((Float.parseFloat(getTotalInternalMemorySize().replace(",", "")) - Float.parseFloat(getAvailableInternalMemorySize().replace(",", ""))) / Float.parseFloat(getTotalInternalMemorySize().replace(",", "")) * 100);
                Log.d("xxx", "2" + progress);
                if (progress <= 70) {
                    arc2.setFinishedStrokeColor(getResources().getColor(R.color.green));
                    arc2.setTextColor(getResources().getColor(R.color.green));
                } else if (progress > 70 && progress < 80) {
                    arc2.setFinishedStrokeColor(getResources().getColor(R.color.warning));
                    arc2.setTextColor(getResources().getColor(R.color.warning));
                } else {
                    arc2.setFinishedStrokeColor(getResources().getColor(R.color.red));
                    arc2.setTextColor(getResources().getColor(R.color.red));
                }
                arc2.setProgress(progress);
            }

            if (externa) {
                if (Build.VERSION.SDK_INT > 18) {
                    externa1.setText(String.valueOf((int) (Float.parseFloat(getTotalExternalMemorySize18().replace(",", "")) - Float.parseFloat(getAvailableExternalMemorySize18().replace(",", "")))));
                    externa2.setText("/" + getTotalExternalMemorySize18().replace(",", ""));
                    int progress = (int) ((Float.parseFloat(getTotalExternalMemorySize18().replace(",", "")) - Float.parseFloat(getAvailableExternalMemorySize18().replace(",", ""))) / Float.parseFloat(getTotalExternalMemorySize18().replace(",", "")) * 100);
                    Log.d("xxx", "3" + progress);
                    if (progress <= 70) {
                        arc3.setFinishedStrokeColor(getResources().getColor(R.color.green));
                        arc3.setTextColor(getResources().getColor(R.color.green));
                    } else if (progress > 70 && progress < 80) {
                        arc3.setFinishedStrokeColor(getResources().getColor(R.color.warning));
                        arc3.setTextColor(getResources().getColor(R.color.warning));
                    } else {
                        arc3.setFinishedStrokeColor(getResources().getColor(R.color.red));
                        arc3.setTextColor(getResources().getColor(R.color.red));
                    }
                    arc3.setProgress(progress);
                } else {
                    externa1.setText(String.valueOf((int) (Float.parseFloat(getTotalExternalMemorySize().replace(",", "")) - Float.parseFloat(getAvailableExternalMemorySize().replace(",", "")))));
                    externa2.setText("/" + getTotalExternalMemorySize().replace(",", ""));
                    int progress = (int) ((Float.parseFloat(getTotalExternalMemorySize().replace(",", "")) - Float.parseFloat(getAvailableExternalMemorySize().replace(",", ""))) / Float.parseFloat(getTotalExternalMemorySize().replace(",", "")) * 100);
                    Log.d("xxx", "4" + progress);
                    if (progress <= 70) {
                        arc3.setFinishedStrokeColor(getResources().getColor(R.color.green));
                        arc3.setTextColor(getResources().getColor(R.color.green));
                    } else if (progress > 70 && progress < 80) {
                        arc3.setFinishedStrokeColor(getResources().getColor(R.color.warning));
                        arc3.setTextColor(getResources().getColor(R.color.warning));
                    } else {
                        arc3.setFinishedStrokeColor(getResources().getColor(R.color.red));
                        arc3.setTextColor(getResources().getColor(R.color.red));
                    }
                    arc3.setProgress(progress);
                }
            }

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

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static String getAvailableInternalMemorySize18() {
        if (Build.VERSION.SDK_INT > 18) {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return formatSize(availableBlocks * blockSize);
        }
        return "ERROR";
    }

    public static String getTotalInternalMemorySize18() {
        if (Build.VERSION.SDK_INT > 18) {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long totalBlocks = stat.getTotalBytes();
            return formatSize(totalBlocks);
        }
        return "ERROR";
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

    public static String getAvailableExternalMemorySize18() {
        if (externalMemoryAvailable()) {
            if (Build.VERSION.SDK_INT > 18) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long availableBlocks = stat.getAvailableBytes();
                return formatSize(availableBlocks);
            } else {
                return "ERROR";
            }
        }
        return "ERROR";
    }

    public static String getTotalExternalMemorySize18() {
        if (externalMemoryAvailable()) {
            if (Build.VERSION.SDK_INT > 18) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long totalBlocks = stat.getTotalBytes();
                return formatSize(totalBlocks);
            }
        } else {
            return "ERROR";
        }
        return "ERROR";
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