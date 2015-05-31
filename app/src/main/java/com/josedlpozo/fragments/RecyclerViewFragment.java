package com.josedlpozo.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.josedlpozo.adapters.RecyclerViewAdapter;
import com.josedlpozo.database.AppDbAdapter;
import com.josedlpozo.database.AppsDbHelper;
import com.josedlpozo.listeners.EndlessRecyclerOnScrollListener;
import com.josedlpozo.optimiza.AppActivity;
import com.josedlpozo.optimiza.AppsPermisos;
import com.josedlpozo.optimiza.R;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;


public class RecyclerViewFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ScaleInAnimationAdapter sAdapter;

    private ArrayList<AppsPermisos> mContentItems = new ArrayList<>();

    private int contador_apps = 0;

    public static RecyclerViewFragment newInstance() {
        return new RecyclerViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        AppDbAdapter db = new AppDbAdapter(getActivity().getBaseContext());
        AppsDbHelper dbHelper = new AppsDbHelper(getActivity().getBaseContext());

        SQLiteDatabase dbSQ = dbHelper.getWritableDatabase();

        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        /*fab.attachToRecyclerView(mRecyclerView, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                Log.d("ListViewFragment", "onScrollDown()");
            }

            @Override
            public void onScrollUp() {
                Log.d("ListViewFragment", "onScrollUp()");
            }
        });*/

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);


        Cursor cursor = dbSQ.rawQuery("SELECT * FROM " + "Permisos_App LIMIT " + contador_apps + ",10", null);
        if (cursor == null) {
            Toast.makeText(getActivity().getBaseContext(), "No hay aplicaciones para mostrar.", Toast.LENGTH_LONG).show();
        } else {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                AppsPermisos app = null;
                try {
                    app = new AppsPermisos(getActivity().getPackageManager().getApplicationIcon(cursor.getString(cursor.getColumnIndex(db.COLUMNA_PAQUETES))), cursor.getString(cursor.getColumnIndex(db.COLUMNA_NOMBRE)), getActivity().getPackageManager().getPackageInfo(cursor.getString(cursor.getColumnIndex(db.COLUMNA_PAQUETES)), PackageManager.GET_PERMISSIONS).requestedPermissions, cursor.getString(cursor.getColumnIndex(db.COLUMNA_PAQUETES)));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                mContentItems.add(app);
            }
            contador_apps += 10;
            dbSQ.close();

        }
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mContentItems);
        mAdapter = new RecyclerViewMaterialAdapter(adapter);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
        sAdapter = new ScaleInAnimationAdapter(alphaAdapter);
        mRecyclerView.setAdapter(sAdapter);

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("DemoRecView", "Pulsado el elemento " + mContentItems.get(mRecyclerView.getChildAdapterPosition(v) - 1).getNombre());
                AppsPermisos app = mContentItems.get(mRecyclerView.getChildAdapterPosition(v) - 1);
                Bundle bundle = new Bundle();
                Log.d("xxx", "+" + app.getRequestedPermissions().toString());
                bundle.putStringArray("PERMISOS", app.getRequestedPermissions());
                bundle.putString("PAQUETE", app.getNombrePaquete());
                bundle.putString("NOMBRE", app.getNombre());

                Intent intent = new Intent(getActivity(), AppActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        EndlessRecyclerOnScrollListener myRecyclerViewOnScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.d("xxx", "ONLOADMORE");
                loadMoreData(current_page);
            }

            @Override
            public void showFAB(int mostrar) {
                if (mostrar == 0) fab.setVisibility(View.INVISIBLE);
                else fab.setVisibility(View.VISIBLE);
            }
        };
        mRecyclerView.setOnScrollListener(myRecyclerViewOnScrollListener);

        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, myRecyclerViewOnScrollListener);


    }


    // adding 10 object creating dymically to arraylist and updating recyclerview when ever we reached last item
    private void loadMoreData(int current_page) {
        AppDbAdapter db = new AppDbAdapter(getActivity().getBaseContext());
        AppsDbHelper dbHelper = new AppsDbHelper(getActivity().getBaseContext());

        SQLiteDatabase dbSQ = dbHelper.getWritableDatabase();
        Cursor cursor = dbSQ.rawQuery("SELECT * FROM " + "Permisos_App LIMIT " + contador_apps + ",10", null);
        if (cursor == null) {
            Toast.makeText(getActivity().getBaseContext(), "No hay aplicaciones para mostrar.", Toast.LENGTH_LONG).show();
        } else {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                AppsPermisos app = null;
                try {
                    app = new AppsPermisos(getActivity().getPackageManager().getApplicationIcon(cursor.getString(cursor.getColumnIndex(db.COLUMNA_PAQUETES))), cursor.getString(cursor.getColumnIndex(db.COLUMNA_NOMBRE)), getActivity().getPackageManager().getPackageInfo(cursor.getString(cursor.getColumnIndex(db.COLUMNA_PAQUETES)), PackageManager.GET_PERMISSIONS).requestedPermissions, cursor.getString(cursor.getColumnIndex(db.COLUMNA_PAQUETES)));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                mContentItems.add(app);
            }
            contador_apps += 10;
            dbSQ.close();
            Log.i("XXX", "LLEGA" + contador_apps);
            sAdapter.notifyDataSetChanged();

        }
    }
}