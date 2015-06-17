package com.josedlpozo.adapters;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.josedlpozo.optimiza.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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
        private String packages;
        private int tipo;
        private Button play;

        private boolean checked = false;

        int v = 0;

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
            } else {
                packages = t;
                try {
                    final String paquete = itemView.getContext().getPackageManager().getApplicationInfo(t, 0).packageName;
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
                    if (!checked) {
                        new Thread(new Runnable() {
                            public void run() {
                                Log.d("yyy", "RUN" + "https://play.google.com/store/apps/details?id=" + paquete);
                                if (ping("https://play.google.com/store/apps/details?id=" + paquete, 200000)) {
                                    checked = true;
                                    v = 0;
                                } else {
                                    checked = true;
                                    v = 1;
                                }
                                Thread.interrupted();
                            }
                        }).start();
                    }
                    setVisibility(v);
                    play.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                // Google Play
                                String packageName = itemView.getContext().getPackageManager().getApplicationInfo(packages, 0).packageName;
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
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

        public void setVisibility(int v) {
            if (v == 0)
                play.setVisibility(View.VISIBLE);
            else play.setVisibility(View.GONE);
        }

        public static boolean ping(String url, int timeout) {
            // Otherwise an exception may be thrown on invalid SSL certificates:
            //url = url.replaceFirst("^https", "http");

            try {
                /*HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setConnectTimeout(timeout);
                connection.setReadTimeout(timeout);
                connection.setRequestMethod("HEAD");
                int responseCode = connection.getResponseCode();
                Log.d("xxx",""+responseCode);
                return (200 == responseCode );*/
                URL urlA = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlA.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int code = connection.getResponseCode();
                Log.d("xx", "" + code);
                if (code == 200) {
                    return true;
                }
                return false;
            } catch (IOException exception) {
                return false;
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

}
