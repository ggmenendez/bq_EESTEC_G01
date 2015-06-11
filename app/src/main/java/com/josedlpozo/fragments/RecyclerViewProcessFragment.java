package com.josedlpozo.fragments;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.josedlpozo.adapters.RecyclerViewProcessAdapter;
import com.josedlpozo.optimiza.R;
import com.josedlpozo.taskmanager.DetailProcess;
import com.josedlpozo.taskmanager.PackagesInfo;
import com.josedlpozo.taskmanager.ProcessInfo;
import com.melnykov.fab.FloatingActionButton;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

/**
 * Created by josedlpozo on 9/6/15.
 */
public class RecyclerViewProcessFragment extends Fragment {

    private static final String TAG = "Process";

    private ProcessInfo pinfo = null;
    public ActivityManager am = null;
    private PackagesInfo packageinfo = null;
    // private List<RunningTaskInfo> tasklist = null;
    private PackageManager pm;

    private static final int STAT_TASK = 0;
    private static final int STAT_SERVICE = 1;
    private static final int STAT_SYSTEM = 2;

    private int currentStat = STAT_TASK;
    private ArrayList<DetailProcess> listdp;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ScaleInAnimationAdapter sAdapter;

    private FloatingActionButton fab;

    public static final int MENU_CANCEL = 0;
    public static final int MENU_SWITCH = 1;
    public static final int MENU_KILL = 2;
    public static final int MENU_DETAIL = 3;
    public static final int MENU_UNINSTALL = 4;
    public static final int MENU_KILL_ALL = 5;
    private BroadcastReceiver loadFinish = new LoadFinishReceiver();

    protected static final String ACTION_LOAD_FINISH = "org.freecoder.taskmanager.ACTION_LOAD_FINISH";

    public RecyclerViewProcessAdapter adapter;

    public long availMem1 = 0;
    public long availMem2 = 0;

    public int contador = 0;



    public static RecyclerViewProcessFragment newInstance() {
        return new RecyclerViewProcessFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview_process, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_process);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        fab = (FloatingActionButton) view.findViewById(R.id.fab_process);
        final Animation animRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_rotate);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.startAnimation(animRotate);
                refresh();
                sAdapter.notifyDataSetChanged();
            }
        });

        am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        pm = getActivity().getApplicationContext().getPackageManager();
        packageinfo = new PackagesInfo(getActivity());
        pinfo = new ProcessInfo();
        refresh();
        getRunningProcess();


        adapter = new RecyclerViewProcessAdapter(listdp);
        mAdapter = new RecyclerViewMaterialAdapter(adapter);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
        sAdapter = new ScaleInAnimationAdapter(alphaAdapter);
        mRecyclerView.setAdapter(sAdapter);

        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(1000);
        mRecyclerView.getItemAnimator().setRemoveDuration(1000);
        mRecyclerView.getItemAnimator().setMoveDuration(1000);
        mRecyclerView.getItemAnimator().setChangeDuration(1000);

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("DemoRecView", "Pulsado el elemento " + listdp.get(mRecyclerView.getChildAdapterPosition(v) - 1).getNombre());
                final DetailProcess dp = listdp.get(mRecyclerView.getChildAdapterPosition(v) - 1);
                AlertDialog alert = new AlertDialog.Builder(getActivity()).setTitle(dp.getTitle()).setItems(
                        R.array.menu_task_operation, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case MENU_KILL: {
                                        am.restartPackage(dp.getPackageName());
                                        if (dp.getPackageName().equals(getActivity().getPackageName()))
                                            return;
                                        refresh();
                                        return;
                                    }
                                    case MENU_SWITCH: {
                                        if (dp.getPackageName().equals(getActivity().getPackageName()))
                                            return;
                                        Intent i = dp.getIntent();
                                        if (i == null) {
                                            Toast.makeText(getActivity(), R.string.message_switch_fail, Toast.LENGTH_LONG)
                                                    .show();
                                            return;
                                        }
                                        try {
                                            startActivity(i);
                                        } catch (Exception ee) {
                                            Toast.makeText(getActivity(), ee.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                        return;
                                    }
                                    case MENU_UNINSTALL: {
                                        Uri uri = Uri.fromParts("package", dp.getPackageName(), null);
                                        Intent it = new Intent(Intent.ACTION_DELETE, uri);
                                        try {
                                            startActivity(it);
                                        } catch (Exception e) {
                                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                        return;
                                    }
                                    case MENU_DETAIL: {
                                        Intent detailsIntent = new Intent();
                                        detailsIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                                        detailsIntent.putExtra("com.android.settings.ApplicationPkgName", dp.getPackageName());
                                        startActivity(detailsIntent);

                                        //Uri uri = Uri.parse("market://search?q=pname:" + );
                                        //Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                        // try {
                                        // ctx.startActivity(detailsIntent);
                                        // } catch (Exception e) {
                                        // Toast.makeText(ctx, R.string.message_no_market,
                                        // Toast.LENGTH_LONG).show();
                                        // }
                                        return;
                                    }
                                }

                        /* User clicked so do some stuff */
                                // String[] items =
                                // ctx.getResources().getStringArray(R.array.menu_task_operation);
                                // Toast.makeText(ctx, "You selected: " + which + " , " + items[which],
                                // Toast.LENGTH_SHORT).show();
                            }
                        }).create();
                alert.show();

            }
        });

    }

    public ProcessInfo getProcessInfo() {
        return pinfo;
    }

    public PackagesInfo getPackageInfo() {
        return packageinfo;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ACTION_LOAD_FINISH);
        getActivity().registerReceiver(loadFinish, filter);
        packageinfo = new PackagesInfo(getActivity());
        // Make sure the progress bar is visible

        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(loadFinish);
    }

    @SuppressWarnings("unchecked")
    public void getRunningProcess() {
        List<ActivityManager.RunningAppProcessInfo> list2 = am.getRunningAppProcesses();
        listdp = new ArrayList<DetailProcess>();
        for (ActivityManager.RunningAppProcessInfo ti : list2) {
            // System.out.println(ti.processName + "/" + ti.pid + "/" + ti.lru + "/" + ti.importance
            // + "/"
            // + Arrays.toString(ti.pkgList) + "\n\n");
            if (ti.processName.contains("system") || ti.processName.equals("com.android.phone") || ti.processName.contains("acore") || ti.processName.contains("settings") || ti.processName.contains("Sistema")) {
                continue;
            }
            Log.d("XXX", ti.processName);
            DetailProcess dp = new DetailProcess(getActivity(), ti);
            dp.fetchApplicationInfo(packageinfo);
            dp.fetchPackageInfo();
            dp.fetchPsRow(pinfo);
            // dp.fetchTaskInfo(this);
            if (dp.isGoodProcess()) {
                listdp.add(dp);
                // System.out.println(Arrays.toString(dp.getPkginfo().activities));
            }
        }
        //Collections.sort(listdp);
        if (!listdp.isEmpty()) {
            Collections.sort(listdp, new Comparator<DetailProcess>() {
                @Override
                public int compare(DetailProcess lhs, DetailProcess rhs) {
                    return rhs.getPsrow().mem - lhs.getPsrow().mem;
                }
            });
        }
        //sAdapter.notifyDataSetChanged();
        adapter = new RecyclerViewProcessAdapter(listdp);
    }

    public void refresh() {
        if (currentStat == STAT_TASK) {

            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    pinfo = new ProcessInfo();
                    getRunningProcess();
                    Intent in = new Intent(ACTION_LOAD_FINISH);
                    getActivity().sendBroadcast(in);
                }

            });
            t.start();
        }
        // tasklist = am.getRunningTasks(100);
    }


    private class LoadFinishReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context ctx, Intent intent) {
            mAdapter = new RecyclerViewMaterialAdapter(adapter);
            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
            sAdapter = new ScaleInAnimationAdapter(alphaAdapter);
            sAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(sAdapter);
            ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            long avail = memoryInfo.availMem;

            if (Build.VERSION.SDK_INT > 16) {
                long total = memoryInfo.totalMem;
                float division = (float) avail / total;
                Log.d("jj", "A:" + avail + "  -- T:" + total + " -- D:" + division);
                Log.d("TOTAL", "" + division * 100.0);
            }
            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.i("DemoRecView", "Pulsado el elemento " + listdp.get(mRecyclerView.getChildAdapterPosition(v) - 1).getNombre());
                    final DetailProcess dp = listdp.get(mRecyclerView.getChildAdapterPosition(v) - 1);
                    AlertDialog alert = new AlertDialog.Builder(getActivity()).setTitle(dp.getTitle()).setItems(
                            R.array.menu_task_operation, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case MENU_KILL: {
                                            if (dp.getPackageName().equals(getActivity().getPackageName()))
                                                return;
                                            Log.d("yyy", dp.getPackageName());
                                            am.restartPackage(dp.getPackageName());

                                            ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
                                            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                                            activityManager.getMemoryInfo(memoryInfo);
                                            availMem2 = memoryInfo.availMem;
                                            Toast.makeText(getActivity(), "He liberado: " + (availMem2 - availMem1) / (1024.0 * 1024.0), Toast.LENGTH_SHORT).show();
                                            contador = 0;
                                            refresh();
                                            return;
                                        }
                                        case MENU_SWITCH: {
                                            if (dp.getPackageName().equals(getActivity().getPackageName()))
                                                return;
                                            Intent i = dp.getIntent();
                                            if (i == null) {
                                                Toast.makeText(getActivity(), R.string.message_switch_fail, Toast.LENGTH_LONG)
                                                        .show();
                                                return;
                                            }
                                            try {
                                                startActivity(i);
                                            } catch (Exception ee) {
                                                Toast.makeText(getActivity(), ee.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                            return;
                                        }
                                        case MENU_UNINSTALL: {
                                            Uri uri = Uri.fromParts("package", dp.getPackageName(), null);
                                            Intent it = new Intent(Intent.ACTION_DELETE, uri);
                                            try {
                                                startActivity(it);
                                            } catch (Exception e) {
                                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                            return;
                                        }
                                        case MENU_DETAIL: {
                                            Intent detailsIntent = new Intent();
                                            detailsIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                                            detailsIntent.putExtra("com.android.settings.ApplicationPkgName", dp.getPackageName());
                                            startActivity(detailsIntent);

                                            //Uri uri = Uri.parse("market://search?q=pname:" + );
                                            //Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                            // try {
                                            // ctx.startActivity(detailsIntent);
                                            // } catch (Exception e) {
                                            // Toast.makeText(ctx, R.string.message_no_market,
                                            // Toast.LENGTH_LONG).show();
                                            // }
                                            return;
                                        }
                                        case MENU_KILL_ALL: {
                                            for (DetailProcess dp : listdp) {
                                                if (dp.getPackageName().equals(getActivity().getPackageName()))
                                                    continue;
                                                Log.d("yyy", dp.getPackageName());
                                                am.restartPackage(dp.getPackageName());
                                            }
                                            ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
                                            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                                            activityManager.getMemoryInfo(memoryInfo);
                                            availMem2 = memoryInfo.availMem;
                                            Toast.makeText(getActivity(), "He liberado: " + (availMem2 - availMem1) / (1024.0 * 1024.0), Toast.LENGTH_SHORT).show();
                                            contador = 0;
                                            refresh();
                                            return;
                                        }
                                    }

                        /* User clicked so do some stuff */
                                    // String[] items =
                                    // ctx.getResources().getStringArray(R.array.menu_task_operation);
                                    // Toast.makeText(ctx, "You selected: " + which + " , " + items[which],
                                    // Toast.LENGTH_SHORT).show();
                                }
                            }).create();
                    alert.show();

                }
            });
            memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            if (contador == 0) {
                availMem1 = memoryInfo.availMem;
            }
            contador++;
            Toast.makeText(getActivity(), getTotalRAM(), Toast.LENGTH_SHORT).show();
            sAdapter.notifyDataSetChanged();
            MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
        }

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
                lastValue = twoDecimalForm.format(tb).concat(" TB");
            } else if (gb > 1) {
                lastValue = twoDecimalForm.format(gb).concat(" GB");
            } else if (mb > 1) {
                lastValue = twoDecimalForm.format(mb).concat(" MB");
            } else {
                lastValue = twoDecimalForm.format(totRam).concat(" KB");
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Streams.close(reader);
        }

        return lastValue;
    }


}