package com.josedlpozo.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.AppsViewHolder> implements View.OnClickListener {

    private ArrayList<AppsPermisos> datos;

    private View.OnClickListener listener;

    public static class AppsViewHolder
            extends RecyclerView.ViewHolder {

        private ImageView img;
        private TextView txtName;
        private TextView txtNum;
        private String[] permisos;

        public AppsViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            txtName = (TextView) itemView.findViewById(R.id.name);
            txtNum = (TextView) itemView.findViewById(R.id.num);
        }

        public void bindTitular(AppsPermisos t) {
            Log.i("HOLDER", t.getNombre());
            txtName.setText(t.getNombre());
            txtNum.setText(" " + t.getNumPermisos());
            img.setImageDrawable(t.getImagen());
            permisos = t.getRequestedPermissions();
        }

        public ImageView getImg() {
            return img;
        }

        public TextView getTxtName() {
            return txtName;
        }

        public TextView getTxtNum() {
            return txtNum;
        }

        public String[] getPermisos() {
            return permisos;
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
            case 0:
                return TYPE_HEADER;
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

        Log.i("HOLDER", " " + viewType);

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

        Log.i("HOLDER", "" + position);
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

