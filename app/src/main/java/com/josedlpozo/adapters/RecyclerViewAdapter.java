package com.josedlpozo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.josedlpozo.optimiza.AppsPermisos;
import com.josedlpozo.optimiza.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by josedlpozo on 12/6/15.
 * <p/>
 * Adapter para recyclerview de aplicaciones instaladas con numero de permisos.
 * <p/>
 * Cada app tiene icono, nombre y numero de permisos. Implementa ClickListener para recoger eventos de toque y
 * avanzar a vista detallada de la aplicación.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.AppsViewHolder> implements View.OnClickListener {

    private ArrayList<AppsPermisos> datos;

    private View.OnClickListener listener;

    public static class AppsViewHolder
            extends RecyclerView.ViewHolder {

        private ImageView img;
        private TextView txtName;
        private TextView txtNum;

        public AppsViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            txtName = (TextView) itemView.findViewById(R.id.name);
            txtNum = (TextView) itemView.findViewById(R.id.num);

        }

        public void bindTitular(AppsPermisos t) {
            txtName.setText(t.getNombre());
            txtNum.setText("Número de permisos: " + t.getNumPermisos());
            img.setImageDrawable(t.getImagen());
        }

    }


    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;

    public RecyclerViewAdapter(ArrayList<AppsPermisos> datos) {
        this.datos = datos;
        Collections.sort(this.datos, new Comparator<AppsPermisos>() {
            @Override
            public int compare(AppsPermisos lhs, AppsPermisos rhs) {
                return rhs.getNumPermisos() - lhs.getNumPermisos();
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            default:
                return TYPE_CELL;
        }
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    @Override
    public AppsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case TYPE_HEADER: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_big, parent, false);
                view.setOnClickListener(this);
                return new AppsViewHolder(view) {
                };
            }
            case TYPE_CELL: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_small, parent, false);
                view.setOnClickListener(this);
                return new AppsViewHolder(view) {
                };
            }
        }
        return null;
    }


    @Override
    public void onBindViewHolder(AppsViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                break;
            case TYPE_CELL:
                break;
        }

        AppsPermisos item = datos.get(position);

        holder.bindTitular(item);

    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null)
            listener.onClick(view);
    }

}

