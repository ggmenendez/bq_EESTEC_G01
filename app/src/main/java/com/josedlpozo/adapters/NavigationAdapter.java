package com.josedlpozo.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.josedlpozo.optimiza.Item_objct;
import com.josedlpozo.optimiza.R;

import java.util.ArrayList;

/**
 * Created by josedlpozo on 12/6/15.
 *
 * Adapter para listview de drawerlayout
 */

public class NavigationAdapter extends BaseAdapter {


    private Activity activity;
    ArrayList<Item_objct> arrayitms;

    public NavigationAdapter(Activity activity, ArrayList<Item_objct> listarry) {
        super();
        this.activity = activity;
        this.arrayitms = listarry;
    }

    @Override
    public Item_objct getItem(int position) {
        return arrayitms.get(position);
    }

    public int getCount() {
        return arrayitms.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //Declaramos clase estatica la cual representa a la fila
    public static class Fila {
        TextView titulo_itm;
        ImageView icono;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Fila view;
        LayoutInflater inflator = activity.getLayoutInflater();
        if (convertView == null) {
            view = new Fila();
            Item_objct itm = arrayitms.get(position);
            convertView = inflator.inflate(R.layout.itm, null);
            view.titulo_itm = (TextView) convertView.findViewById(R.id.title_item);
            view.titulo_itm.setText(itm.getTitulo());
            view.icono = (ImageView) convertView.findViewById(R.id.icon);
            view.icono.setImageResource(itm.getIcono());
            convertView.setTag(view);
        } else {
            view = (Fila) convertView.getTag();
        }
        return convertView;
    }

}