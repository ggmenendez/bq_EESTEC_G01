package com.josedlpozo.adapters;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.josedlpozo.optimiza.R;
import com.josedlpozo.optimiza.WebViewActivity;

import java.util.Date;
import java.util.List;

/**
 * Created by josedlpozo on 12/6/15.
 * <p/>
 * Adapter para recyclerview de aplicacion en vista detallada.
 * <p/>
 * Muestra datos de nombre de paquete, version de la aplicacion, version minima de SDK, fechas de instalación
 * y de actualización, y por último una lista de los permisos que necesita.
 * <p/>
 * Implementa OnClickListener para hacer una petición a Internet y recoger la descripción de cada permiso.
 */


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
        private ImageView img_warning;
        private String packages;
        private int tipo;
        private Button play;

        public AppsViewHolder(View itemView, int type) {
            super(itemView);
            txtPermiso = (TextView) itemView.findViewById(R.id.permiso);
            txtNombre = (TextView) itemView.findViewById(R.id.name);
            txtNum = (TextView) itemView.findViewById(R.id.num);
            img = (ImageView) itemView.findViewById(R.id.img);
            dateFirst = (TextView) itemView.findViewById(R.id.date_first);
            dateLast = (TextView) itemView.findViewById(R.id.date_last);
            version = (TextView) itemView.findViewById(R.id.version);
            sdk = (TextView) itemView.findViewById(R.id.sdk);
            img_warning = (ImageView) itemView.findViewById(R.id.img_warning);
            play = (Button) itemView.findViewById(R.id.play);
            this.tipo = type;
        }

        public void bindTitular(String t) {
            if (tipo == TYPE_CELL) {
                //Busqueda de la primera mayuscula en el permiso, para eliminar "android.permission..." y mostrar solo nombre corto
                int comienzo = 0;
                for (int i = 0; i < t.length(); i++) {
                    if (Character.isUpperCase(t.charAt(i))) {
                        comienzo = i;
                        break;
                    }
                    if (comienzo != 0) break;
                }
                txtPermiso.setText(t.substring(comienzo, t.length()));

                //A implementar, permisos peligrosos!!
                if (t.equals("android.permission.BLUETOOTH") || t.equals("android.permission.BLUETOOTH_ADMIN")) {
                    img_warning.setVisibility(View.VISIBLE);
                    img_warning.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(v.getContext(), "MALO", Toast.LENGTH_LONG).show();
                        }
                    });
                    img_warning.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_warning_black_36dp));
                }
            } else {
                packages = t;
                try {
                    //Creacion del header con los datos importantes de la aplicación.
                    txtNombre.setText(itemView.getContext().getPackageManager().getApplicationLabel(itemView.getContext().getPackageManager().getApplicationInfo(t, 0)));
                    txtNum.setText("Paquete : " + itemView.getContext().getPackageManager().getApplicationInfo(t, 0).packageName);
                    version.setText("Version : " + itemView.getContext().getPackageManager().getPackageInfo(t, 0).versionCode);
                    Date first = new Date(itemView.getContext().getPackageManager().getPackageInfo(t, 0).firstInstallTime);
                    Date last = new Date(itemView.getContext().getPackageManager().getPackageInfo(t, 0).lastUpdateTime);
                    dateFirst.setText("Instalado : " + first.toString());
                    dateLast.setText("Actualizado : " + last.toString());
                    sdk.setText("SDK minimo : " + itemView.getContext().getPackageManager().getApplicationInfo(t, 0).targetSdkVersion);
                    img.setImageDrawable(itemView.getContext().getPackageManager().getApplicationIcon(t));
                    play.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(v.getContext(), WebViewActivity.class);
                                intent.putExtra("PACKAGE", itemView.getContext().getPackageManager().getApplicationInfo(packages, 0).packageName);
                                v.getContext().startActivity(intent);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }


                        }
                    });
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


        switch (viewType) {
            case TYPE_HEADER: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_big, parent, false);
                //view.setOnClickListener(this);
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
