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
import com.josedlpozo.optimiza.AppActivity;
import com.josedlpozo.optimiza.AppsPermisos;
import com.josedlpozo.optimiza.R;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;


public class RecyclerViewFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private ArrayList<AppsPermisos> mContentItems = new ArrayList<>();

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

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.attachToRecyclerView(mRecyclerView, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                Log.d("ListViewFragment", "onScrollDown()");
            }

            @Override
            public void onScrollUp() {
                Log.d("ListViewFragment", "onScrollUp()");
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        Cursor cursor = dbSQ.rawQuery("SELECT * FROM " + "Permisos_App", null);
        if (cursor == null) {
            Toast.makeText(getActivity().getBaseContext(), "PUTO NULL", Toast.LENGTH_LONG).show();
        } else {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                AppsPermisos app = null;
                try {
                    app = new AppsPermisos(getActivity().getPackageManager().getApplicationIcon(cursor.getString(cursor.getColumnIndex(db.COLUMNA_PAQUETES))), cursor.getString(cursor.getColumnIndex(db.COLUMNA_NOMBRE)), dbHelper.convertStringToArray(cursor.getString(cursor.getColumnIndex(db.COLUMNA_PERMISOS))), cursor.getString(cursor.getColumnIndex(db.COLUMNA_PAQUETES)));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                mContentItems.add(app);
            }
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(mContentItems);
            mAdapter = new RecyclerViewMaterialAdapter(adapter);
            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
            mRecyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));

            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("DemoRecView", "Pulsado el elemento " + mContentItems.get(mRecyclerView.getChildAdapterPosition(v) - 1).getNombre());
                    AppsPermisos app = mContentItems.get(mRecyclerView.getChildAdapterPosition(v) - 1);
                    Bundle bundle = new Bundle();
                    bundle.putStringArray("PERMISOS", app.getRequestedPermissions());
                    bundle.putString("PAQUETE", app.getNombrePaquete());
                    bundle.putString("NOMBRE", app.getNombre());

                    Intent intent = new Intent(getActivity(), AppActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
        }
    }
}