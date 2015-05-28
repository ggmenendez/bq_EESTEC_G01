package com.josedlpozo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.josedlpozo.adapters.RecyclerViewPermisosAdapter;
import com.josedlpozo.optimiza.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;
import me.drakeet.materialdialog.MaterialDialog;


public class RecyclerViewFragmentPermisos extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private List<String> mContentItems = new ArrayList<>();
    private MaterialDialog mMaterialDialog;

    public static RecyclerViewFragmentPermisos newInstance() {
        return new RecyclerViewFragmentPermisos();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        String[] permisos = getArguments().getStringArray("PERMISOS");
        mContentItems = Arrays.asList(permisos);


        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);


        RecyclerViewPermisosAdapter adapter = new RecyclerViewPermisosAdapter(mContentItems);
        mAdapter = new RecyclerViewMaterialAdapter(adapter);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
        mRecyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("DemoRecView", "Pulsado el elemento " + mContentItems.get(mRecyclerView.getChildAdapterPosition(v) - 1).toString());
                int longitud = mContentItems.get(mRecyclerView.getChildAdapterPosition(v) - 1).length();
                mMaterialDialog = new MaterialDialog(getActivity());
                mMaterialDialog.setTitle(mContentItems.get(mRecyclerView.getChildAdapterPosition(v) - 1).toString().substring(19, longitud))
                        .setMessage("hola chabo").show();

            }
        });

        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
    }
}
