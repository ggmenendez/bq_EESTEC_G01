package com.josedlpozo.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.josedlpozo.optimiza.R;

import java.util.List;


public class RecyclerViewPermisosAdapter extends RecyclerView.Adapter<RecyclerViewPermisosAdapter.AppsViewHolder> implements View.OnClickListener {

    private List<String> datos;

    private View.OnClickListener listener;

    public static class AppsViewHolder
            extends RecyclerView.ViewHolder {

        private TextView txtPermiso;

        public AppsViewHolder(View itemView) {
            super(itemView);
            txtPermiso = (TextView) itemView.findViewById(R.id.description);

        }

        public void bindTitular(String t) {
            Log.i("PERMISOS", t);
            int comienzo = 0;
            for (int i = 0; i < t.length(); i++) {
                if (Character.isUpperCase(t.charAt(i))) {
                    comienzo = i;
                    break;
                }
                if (comienzo != 0) break;
            }
            txtPermiso.setText(t.substring(comienzo, t.length()));
        }


    }


    static final int TYPE_CELL = 1;

    public RecyclerViewPermisosAdapter(List<String> datos) {
        this.datos = datos;
        Log.i("PERMISOS", datos.get(0));
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

        Log.i("HOLDER", " " + viewType);

        switch (viewType) {
            case TYPE_CELL: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_small_permisos, parent, false);
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
            case TYPE_CELL:
                break;
        }

        String item = datos.get(position);
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
