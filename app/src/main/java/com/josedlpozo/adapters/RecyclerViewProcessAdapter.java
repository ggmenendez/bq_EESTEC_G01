package com.josedlpozo.adapters;

/**
 * Created by josedlpozo on 9/6/15.
 */

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.josedlpozo.optimiza.R;
import com.josedlpozo.taskmanager.DetailProcess;
import com.josedlpozo.taskmanager.ProcessInfo;

import java.util.ArrayList;

public class RecyclerViewProcessAdapter extends RecyclerView.Adapter<RecyclerViewProcessAdapter.AppsViewHolder> implements View.OnClickListener {

    private ArrayList<DetailProcess> datos;

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

        public void bindTitular(DetailProcess t) {
            Log.i("HOLDER", "JOSE");
            img.setImageDrawable(t.getAppinfo().loadIcon(itemView.getContext().getPackageManager()));
            txtName.setText(t.getTitle());
            ProcessInfo.PsRow row = t.getPsrow();
            if (row == null) {
                txtNum.setText(R.string.memory_unknown);
            } else {
                txtNum.setText("Memoria: " + (int) Math.ceil(row.mem / 1024) + "K");
            }
        }

    }


    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;


    public RecyclerViewProcessAdapter(ArrayList<DetailProcess> datos) {
        this.datos = datos;

    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            //case 0:
            //  return TYPE_HEADER;
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
        DetailProcess item = datos.get(position);

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