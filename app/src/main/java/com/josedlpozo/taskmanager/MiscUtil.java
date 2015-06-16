package com.josedlpozo.taskmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.widget.Toast;

import com.josedlpozo.fragments.ProcessesFragment;
import com.josedlpozo.optimiza.R;

public class MiscUtil {

    public static final int MENU_CANCEL = 0;
    public static final int MENU_SWITCH = 1;
    public static final int MENU_KILL = 2;
    public static final int MENU_DETAIL = 3;
    public static final int MENU_UNINSTALL = 4;

    public static PackageInfo getPackageInfo(PackageManager pm, String name) {
        PackageInfo ret = null;
        try {
            ret = pm.getPackageInfo(name, PackageManager.GET_ACTIVITIES);
        } catch (NameNotFoundException e) {
            // e.printStackTrace();
        }
        return ret;
    }

    public static Dialog getTaskMenuDialog(final ProcessesFragment ctx, final DetailProcess dp) {

        return new AlertDialog.Builder(ctx.getActivity()).setTitle(dp.getTitle()).setItems(
                R.array.menu_task_operation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case MENU_KILL: {
                                ctx.am.restartPackage(dp.getPackageName());
                                if (dp.getPackageName().equals(ctx.getActivity().getPackageName()))
                                    return;
                                ctx.refresh();
                                return;
                            }
                            case MENU_SWITCH: {
                                if (dp.getPackageName().equals(ctx.getActivity().getPackageName()))
                                    return;
                                Intent i = dp.getIntent();
                                if (i == null) {
                                    Toast.makeText(ctx.getActivity(), R.string.message_switch_fail, Toast.LENGTH_LONG)
                                            .show();
                                    return;
                                }
                                try {
                                    ctx.startActivity(i);
                                } catch (Exception ee) {
                                    Toast.makeText(ctx.getActivity(), ee.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                return;
                            }
                            case MENU_UNINSTALL: {
                                Uri uri = Uri.fromParts("package", dp.getPackageName(), null);
                                Intent it = new Intent(Intent.ACTION_DELETE, uri);
                                try {
                                    ctx.startActivity(it);
                                } catch (Exception e) {
                                    Toast.makeText(ctx.getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                return;
                            }
                            case MENU_DETAIL: {
                                Intent detailsIntent = new Intent();
                                detailsIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                                detailsIntent.putExtra("com.android.settings.ApplicationPkgName", dp.getPackageName());
                                ctx.startActivity(detailsIntent);

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
    }
}
