package com.josedlpozo.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.josedlpozo.adapters.RecyclerViewPermisosAdapter;
import com.josedlpozo.database.AppsDbHelper;
import com.josedlpozo.database.PermisosAdapter;
import com.josedlpozo.optimiza.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by josedlpozo on 12/6/15.
 * <p/>
 * Fragment para vista detallada de aplicacion
 * <p/>
 * Muestra datos de nombre de paquete, version de la aplicacion, version minima de SDK, fechas de instalación
 * y de actualización, y por último una lista de los permisos que necesita.
 * <p/>
 * Encargada de pedir a internet el JSON con los datos de la descripción de cada permiso.
 */

public class RecyclerViewFragmentPermisos extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ScaleInAnimationAdapter sAdapter;

    private List<String> mContentItems = new ArrayList<>();
    private MaterialDialog mMaterialDialog;

    // URL del json
    public static final String INFO_FILE = "https://dl.dropbox.com/s/ect48h40rx5ul3y/jose.txt?dl=0";


    private String permiso = "";
    private String descripcion = "No disponible";

    // Espera de descarga de descripcion.
    private CircularProgressBar progress;

    Handler handler;



    public static RecyclerViewFragmentPermisos newInstance() {
        return new RecyclerViewFragmentPermisos();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        String[] permisos = getArguments().getStringArray("PERMISOS");
        String paquete = getArguments().getString("PAQUETE");

        // Conversion de array de String a ArrayList los permisos.
        for (String i : permisos) {
            mContentItems.add(i);
        }

        // Ponemos en primera posicion el nombre del paquete para despues inflar el header adecuadamente
        mContentItems.add(0, paquete);

        return inflater.inflate(R.layout.fragment_recyclerview_app, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_app);
        progress = (CircularProgressBar) view.findViewById(R.id.circularProgress);

        // LayoutManaget para recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        // Adapter y sus animaciones
        RecyclerViewPermisosAdapter adapter = new RecyclerViewPermisosAdapter(mContentItems);
        mAdapter = new RecyclerViewMaterialAdapter(adapter);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
        sAdapter = new ScaleInAnimationAdapter(alphaAdapter);
        mRecyclerView.setAdapter(sAdapter);

        // Handler para mejorar fallo en la animacion de entrada a nueva activity
        handler = new Handler();



        handler.postDelayed(r, 700);

        /**
         * Implementa OnClick para buscar en la base de datos de permisos el permiso clickado, si no está en la base de datos
         * hace una petición para descargar el json.
         */
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int longitud = mContentItems.get(mRecyclerView.getChildAdapterPosition(v) - 1).length();
                permiso = mContentItems.get(mRecyclerView.getChildAdapterPosition(v) - 1).toString();
                PermisosAdapter perm = new PermisosAdapter(getActivity());
                AppsDbHelper dbHelper = new AppsDbHelper(getActivity());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor mCursor = db.rawQuery("SELECT PERMISO, DESCRIPCION FROM PERMISOS where PERMISO='" + mContentItems.get(mRecyclerView.getChildAdapterPosition(v) - 1).toString() + "'", null);
                showLoadingProgress();
                if (mCursor.moveToFirst()) {
                    hideLoadingProgress();
                    mMaterialDialog = new MaterialDialog(getActivity());
                    mMaterialDialog.setTitle(permiso.substring(19, longitud))
                            .setMessage(mCursor.getString(mCursor.getColumnIndex(perm.COLUMNA_DESCRIPCION)))
                            .setPositiveButton(
                                    "OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mMaterialDialog.dismiss();
                                            descripcion = "No disponible.";

                                        }
                                    }
                            ).show();
                    db.close();
                } else {
                    showLoadingProgress();
                    new GetJson().execute(permiso);
                }


            }
        });

        // Inicializa animaciones de transicion activity
        setupWindowAnimations();

        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
    }

    Runnable r = new Runnable() {
        public void run() {
            sAdapter.notifyDataSetChanged();
            handler.postDelayed(this, 700);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(r);
    }

    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT > 21) {
            Explode explode = new Explode();
            explode.setDuration(1000);
            getActivity().getWindow().setEnterTransition(explode);

            Fade fade = new Fade();
            fade.setDuration(1000);
            getActivity().getWindow().setReturnTransition(fade);
        }
    }

    public CircularProgressBar getCircularProgressBar() {
        return progress;
    }

    private void hideLoadingProgress() {
        ((CircularProgressDrawable) getCircularProgressBar().getIndeterminateDrawable()).progressiveStop();
        getCircularProgressBar().setVisibility(View.INVISIBLE);

    }

    private void showLoadingProgress() {
        getCircularProgressBar().setVisibility(View.VISIBLE);
        ((CircularProgressDrawable) getCircularProgressBar().getIndeterminateDrawable()).start();
    }

    // AsynTask encargada de la descarga del JSON y de añadir la descripción del permiso a la base de datos
    private class GetJson extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... arg) {

            HttpURLConnection c = null;
            BufferedReader reader = null;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                c = (HttpURLConnection) (new URL(INFO_FILE)).openConnection();
                c.setRequestMethod("GET");
                c.connect();
                c.setReadTimeout(15 * 1000);
                //c.setUseCaches(false);
                reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            String data = stringBuilder.toString();
            JSONObject json = null;
            try {
                json = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Log.d("Create Prediction Request: ", "> " + json);

            if (json != null) {
                try {
                    String descr = json.getString(arg[0]);
                    if (descr != null) {
                        PermisosAdapter perm = new PermisosAdapter(getActivity());
                        AppsDbHelper dbHelper = new AppsDbHelper(getActivity());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues content = new ContentValues();
                        content.put(perm.COLUMNA_PERMISO, arg[0]);
                        content.put(perm.COLUMNA_DESCRIPCION, descr);
                        db = dbHelper.getWritableDatabase();
                        perm.insert(content);
                        db.close();
                        descripcion = descr;
                        Log.i("JSON", descripcion);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "JSON data error!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            hideLoadingProgress();
            mMaterialDialog = new MaterialDialog(getActivity());
            mMaterialDialog.setTitle(permiso.substring(19, permiso.length()))
                    .setMessage(descripcion).setPositiveButton(
                    "OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                            descripcion = "No disponible.";

                        }
                    }
            ).show();
        }
    }
}
