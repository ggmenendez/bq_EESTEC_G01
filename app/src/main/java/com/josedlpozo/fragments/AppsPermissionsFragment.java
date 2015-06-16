package com.josedlpozo.fragments;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.josedlpozo.adapters.RecyclerViewAdapter;
import com.josedlpozo.database.AppDbAdapter;
import com.josedlpozo.database.AppsDbHelper;
import com.josedlpozo.listeners.EndlessRecyclerOnScrollListener;
import com.josedlpozo.optimiza.AppActivity;
import com.josedlpozo.optimiza.AppsPermisos;
import com.josedlpozo.optimiza.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;
import me.drakeet.materialdialog.MaterialDialog;


public class AppsPermissionsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    // Adapter para notificar cambios en los elementos
    private ScaleInAnimationAdapter sAdapter;

    // Lista de AppsPermisos para mostrar
    private ArrayList<AppsPermisos> mContentItems = new ArrayList<>();

    // Numero de apps mostrandose ( numero de apps sacadas de la db mientras se hace scroll )
    private int contador_apps = 0;


    public static AppsPermissionsFragment newInstance() {
        return new AppsPermissionsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // Inicializacion de variables de DB
        AppDbAdapter db = new AppDbAdapter(getActivity().getBaseContext());
        AppsDbHelper dbHelper = new AppsDbHelper(getActivity().getBaseContext());
        SQLiteDatabase dbSQ = dbHelper.getWritableDatabase();

        // Menu desplegable para actualizar, resetear y menu ayuda.
        final FloatingActionMenu menu2 = (FloatingActionMenu) view.findViewById(R.id.menu2);

        FloatingActionButton actualiza = (FloatingActionButton) view.findViewById(R.id.fab12);
        FloatingActionButton ayuda = (FloatingActionButton) view.findViewById(R.id.fab22);
        FloatingActionButton reset = (FloatingActionButton) view.findViewById(R.id.fab32);

        actualiza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu2.close(true);
                actualizaDB();
            }
        });

        ayuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog mMaterialDialog = new MaterialDialog(getActivity());
                mMaterialDialog.setTitle("Ayuda")
                        .setMessage("Pulse actualizar si ha instalado alguna aplicación desde su última visita.\nPulse reset para visualizar de nuevo todas las aplicaciones ignoradas.")
                        .setPositiveButton(
                                "OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mMaterialDialog.dismiss();

                                    }
                                }
                        )
                        .show();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu2.close(true);

                // Reset de flag ignorada
                AppsDbHelper dbHelper = new AppsDbHelper(getActivity().getBaseContext());

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                AppDbAdapter dbAdapter = new AppDbAdapter(getActivity().getBaseContext());

                db.execSQL("UPDATE Permisos_APP set ignorada=0");
                db.close();
                actualizaDB();

            }
        });


        // Inicializacion de layoutmanager para recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);


        // Cargamos 10 primeras apps para mostrar en el recyclerview
        Cursor cursor = dbSQ.rawQuery("SELECT * FROM " + "Permisos_App LIMIT " + contador_apps + ",10", null);
        if (cursor == null) {
            Toast.makeText(getActivity().getBaseContext(), "No hay aplicaciones para mostrar.", Toast.LENGTH_LONG).show();
        } else {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndex(AppDbAdapter.COLUMNA_IGNORADA)) == 0) {
                    AppsPermisos app = null;
                    try {
                        app = new AppsPermisos(getActivity().getPackageManager().getApplicationIcon(cursor.getString(cursor.getColumnIndex(db.COLUMNA_PAQUETES))), cursor.getString(cursor.getColumnIndex(db.COLUMNA_NOMBRE)), getActivity().getPackageManager().getPackageInfo(cursor.getString(cursor.getColumnIndex(db.COLUMNA_PAQUETES)), PackageManager.GET_PERMISSIONS).requestedPermissions, cursor.getString(cursor.getColumnIndex(db.COLUMNA_PAQUETES)));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    mContentItems.add(app);
                }
            }
            contador_apps += 10;
            dbSQ.close();
        }

        // Inicialización de swipe para ignorar aplicaciones de la lista.
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final MaterialDialog mMaterialDialog = new MaterialDialog(getActivity());
                mMaterialDialog.setTitle(mContentItems.get(viewHolder.getAdapterPosition() - 1).getNombre())
                        .setMessage("Está seguro de ignorar " + mContentItems.get(viewHolder.getAdapterPosition() - 1).getNombre() + "? \nMás tarde podrá volver a reestablecerlo en ajustes.")
                        .setPositiveButton(
                                "OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mMaterialDialog.dismiss();
                                        AppDbAdapter db = new AppDbAdapter(getActivity().getBaseContext());
                                        AppsDbHelper dbHelper = new AppsDbHelper(getActivity().getBaseContext());

                                        SQLiteDatabase dbSQ = dbHelper.getWritableDatabase();
                                        dbSQ.execSQL("UPDATE Permisos_APP set " + AppDbAdapter.COLUMNA_IGNORADA + "=1 where " + AppDbAdapter.COLUMNA_NOMBRE + "='" + mContentItems.get(viewHolder.getAdapterPosition() - 1).getNombre() + "'");
                                        mContentItems.remove(viewHolder.getAdapterPosition() - 1);
                                        sAdapter.notifyDataSetChanged();
                                        dbSQ.close();

                                    }
                                }
                        )
                        .setNegativeButton(
                                "CANCEL", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        mMaterialDialog.dismiss();
                                        sAdapter.notifyDataSetChanged();

                                    }
                                }
                        ).show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        // Inicialización de adapters con sus animaciones
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mContentItems);
        mAdapter = new RecyclerViewMaterialAdapter(adapter);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
        sAdapter = new ScaleInAnimationAdapter(alphaAdapter);
        mRecyclerView.setAdapter(sAdapter);
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(1000);
        mRecyclerView.getItemAnimator().setRemoveDuration(1000);
        mRecyclerView.getItemAnimator().setMoveDuration(1000);
        mRecyclerView.getItemAnimator().setChangeDuration(1000);

        // Implementacion de OnClickListener explicado en RecyclerViewAdapter para vista detallada de aplicacion
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppsPermisos app = mContentItems.get(mRecyclerView.getChildAdapterPosition(v) - 1);
                Bundle bundle = new Bundle();
                bundle.putStringArray("PERMISOS", app.getRequestedPermissions());
                bundle.putString("PAQUETE", app.getNombrePaquete());
                bundle.putString("NOMBRE", app.getNombre());

                Intent intent = new Intent(getActivity(), AppActivity.class);
                intent.putExtras(bundle);

                if (Build.VERSION.SDK_INT > 21) {
                    ImageView sharedView = (ImageView) v.findViewById(R.id.img);
                    String transitionName = getString(R.string.transition);
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(), sharedView, transitionName);
                    getActivity().startActivity(intent, transitionActivityOptions.toBundle());
                } else {
                    startActivity(intent);
                }
            }
        });

        // ScrollListener para cargar aplicaciones para mostrar gradualmente, y encargado de mostrar o esconder el fab button menu
        EndlessRecyclerOnScrollListener myRecyclerViewOnScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                ordenaPorNumeroPermisos();
                sAdapter.notifyDataSetChanged();
                loadMoreData(current_page);
            }

            @Override
            public void onHide() {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) menu2.getLayoutParams();
                int fabBottomMargin = lp.bottomMargin;
                menu2.animate().translationY(menu2.getHeight() + fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
            }

            @Override
            public void onShow() {
                menu2.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }
        };
        // Establecemos animaciones de entrada y salida de activity
        setupWindowAnimations();
        mRecyclerView.setOnScrollListener(myRecyclerViewOnScrollListener);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, myRecyclerViewOnScrollListener);


    }

    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT > 20) {
            Explode explode = new Explode();
            explode.setDuration(1000);
            getActivity().getWindow().setExitTransition(explode);

            Fade fade = new Fade();
            fade.setDuration(1000);
            getActivity().getWindow().setReenterTransition(fade);
        }
    }


    // Carga mas aplicaciones en la lista, de 5 en 5
    private void loadMoreData(int current_page) {
        AppDbAdapter db = new AppDbAdapter(getActivity().getBaseContext());
        AppsDbHelper dbHelper = new AppsDbHelper(getActivity().getBaseContext());

        SQLiteDatabase dbSQ = dbHelper.getWritableDatabase();
        Cursor cursor = dbSQ.rawQuery("SELECT * FROM " + "Permisos_App LIMIT " + contador_apps + ",5", null);
        if (cursor == null) {
            Toast.makeText(getActivity().getBaseContext(), "No hay aplicaciones para mostrar.", Toast.LENGTH_LONG).show();
        } else {

            if (cursor.moveToFirst()) {
                while (cursor.moveToNext()) {
                    AppsPermisos app = null;
                    try {
                        if (cursor.getInt(cursor.getColumnIndex(AppDbAdapter.COLUMNA_IGNORADA)) == 0)
                        app = new AppsPermisos(getActivity().getPackageManager().getApplicationIcon(cursor.getString(cursor.getColumnIndex(db.COLUMNA_PAQUETES))), cursor.getString(cursor.getColumnIndex(db.COLUMNA_NOMBRE)), getActivity().getPackageManager().getPackageInfo(cursor.getString(cursor.getColumnIndex(db.COLUMNA_PAQUETES)), PackageManager.GET_PERMISSIONS).requestedPermissions, cursor.getString(cursor.getColumnIndex(db.COLUMNA_PAQUETES)));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    mContentItems.add(app);
                }
            }
            contador_apps += 10;
            dbSQ.close();
            ordenaPorNumeroPermisos();
            sAdapter.notifyDataSetChanged();

        }

    }

    // Ordena la lista de aplicaciones por numero de permisos
    private void ordenaPorNumeroPermisos() {
        Collections.sort(mContentItems, new Comparator<AppsPermisos>() {
            @Override
            public int compare(AppsPermisos lhs, AppsPermisos rhs) {
                return rhs.getNumPermisos() - lhs.getNumPermisos();
            }
        });
    }

    // Actualiza la lista y la base de datos, por si el usuario ha instalado alguna aplicación nueva
    private void actualizaDB() {
        AppsDbHelper dbHelper = new AppsDbHelper(getActivity().getBaseContext());

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        AppDbAdapter dbAdapter = new AppDbAdapter(getActivity().getBaseContext());

        PackageManager pm = getActivity().getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<AppsPermisos> apps = new ArrayList<>();

        for (ApplicationInfo applicationInfo : packages) {

            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);

                //Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;
                if (requestedPermissions == null) continue;
                AppsPermisos app = new AppsPermisos(pm.getApplicationIcon(packageInfo.packageName), pm.getApplicationLabel(applicationInfo).toString(), requestedPermissions, applicationInfo.packageName);

                apps.add(app);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        for (AppsPermisos app : apps) {
            Cursor mCursor = db.rawQuery("SELECT nombre, ignorada FROM Permisos_App where nombre='" + app.getNombre() + "'", null);
            int flag = 0;
            if (!mCursor.moveToFirst()) {
                ContentValues registro = new ContentValues();
                registro.put(AppDbAdapter.COLUMNA_NOMBRE, app.getNombre());
                registro.put(AppDbAdapter.COLUMNA_PAQUETES, app.getNombrePaquete());
                dbAdapter.insert(registro);
                mContentItems.add(app);
                ordenaPorNumeroPermisos();
                sAdapter.notifyDataSetChanged();
            } else if (mCursor.getInt(mCursor.getColumnIndex(AppDbAdapter.COLUMNA_IGNORADA)) == 0) {
                Log.d("acc", mCursor.getString(mCursor.getColumnIndex(AppDbAdapter.COLUMNA_NOMBRE)) + " " + mCursor.getInt(mCursor.getColumnIndex(AppDbAdapter.COLUMNA_IGNORADA)));
                for (AppsPermisos aplicacion : mContentItems) {
                    if (aplicacion.getNombre().equals(app.getNombre())) {
                        flag = 1;
                    }
                }
                if (flag == 0) {
                    mContentItems.add(app);
                }

            }
            Log.d("acc", mCursor.getString(mCursor.getColumnIndex(AppDbAdapter.COLUMNA_NOMBRE)) + " " + mCursor.getInt(mCursor.getColumnIndex(AppDbAdapter.COLUMNA_IGNORADA)));
        }
        ordenaPorNumeroPermisos();
        sAdapter.notifyDataSetChanged();
        db.close();
    }


}