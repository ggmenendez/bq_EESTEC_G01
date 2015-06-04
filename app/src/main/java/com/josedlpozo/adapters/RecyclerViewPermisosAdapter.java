package com.josedlpozo.adapters;

import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.josedlpozo.optimiza.R;

import java.util.Date;
import java.util.List;


public class RecyclerViewPermisosAdapter extends RecyclerView.Adapter<RecyclerViewPermisosAdapter.AppsViewHolder> implements View.OnClickListener {

    private List<String> datos;

    private View.OnClickListener listener;

    public static class AppsViewHolder
            extends RecyclerView.ViewHolder {

        private TextView txtPermiso;
        private TextView txtNombre;
        private TextView txtNum;
        private TextView dateLast;
        private TextView dateFirst;
        private TextView version;
        private TextView sdk;
        private ImageView img;
        private int tipo;

        public AppsViewHolder(View itemView, int type) {
            super(itemView);
            txtPermiso = (TextView) itemView.findViewById(R.id.description);
            txtNombre = (TextView) itemView.findViewById(R.id.name);
            txtNum = (TextView) itemView.findViewById(R.id.num);
            img = (ImageView) itemView.findViewById(R.id.img);
            dateFirst = (TextView) itemView.findViewById(R.id.date_first);
            dateLast = (TextView) itemView.findViewById(R.id.date_last);
            version = (TextView) itemView.findViewById(R.id.version);
            sdk = (TextView) itemView.findViewById(R.id.sdk);
            this.tipo = type;
        }

        public void bindTitular(String t) {
            Log.i("PERMISOS", t);
            if (tipo == TYPE_CELL) {
                /*int comienzo = 0;
                for (int i = 0; i < t.length(); i++) {
                    if (Character.isUpperCase(t.charAt(i))) {
                        comienzo = i;
                        break;
                    }
                    if (comienzo != 0) break;
                }*/
                txtPermiso.setText(t);
            } else {
                try {
                    txtNombre.setText(itemView.getContext().getPackageManager().getApplicationLabel(itemView.getContext().getPackageManager().getApplicationInfo(t, 0)));
                    txtNum.setText("Paquete : " + itemView.getContext().getPackageManager().getApplicationInfo(t, 0).packageName);
                    version.setText("Version : " + itemView.getContext().getPackageManager().getPackageInfo(t, 0).versionCode);
                    Date first = new Date(itemView.getContext().getPackageManager().getPackageInfo(t, 0).firstInstallTime);
                    Date last = new Date(itemView.getContext().getPackageManager().getPackageInfo(t, 0).lastUpdateTime);
                    dateFirst.setText("Instalado : " + first.toString());
                    dateLast.setText("Actualizado : " + last.toString());
                    sdk.setText("SDK minimo : " + itemView.getContext().getPackageManager().getApplicationInfo(t, 0).targetSdkVersion);
                    img.setImageDrawable(itemView.getContext().getPackageManager().getApplicationIcon(t));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;

    public RecyclerViewPermisosAdapter(List<String> datos) {
        this.datos = datos;
        Log.i("PERMISOS", datos.get(0));
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
                return new AppsViewHolder(view, TYPE_HEADER) {
                };
            }
            case TYPE_CELL: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_small_permisos, parent, false);
                view.setOnClickListener(this);
                return new AppsViewHolder(view, TYPE_CELL) {
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

    public boolean isHeader(int position) {
        return position == TYPE_HEADER;
    }

}
