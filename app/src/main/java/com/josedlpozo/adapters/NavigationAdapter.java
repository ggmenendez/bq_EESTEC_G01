package com.josedlpozo.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.josedlpozo.optimiza.DrawerItem;
import com.josedlpozo.optimiza.R;

import java.util.ArrayList;

/**
 * Created by josedlpozo on 12/6/15.
 *
 * Adapter para listview de drawerlayout
 */

public class NavigationAdapter extends BaseAdapter {


    private Activity activity;
    ArrayList<DrawerItem> drawerItems;

    public NavigationAdapter(Activity activity, ArrayList<DrawerItem> drawerItems) {
        super();
        this.activity = activity;
        this.drawerItems = drawerItems;
    }

    @Override
    public DrawerItem getItem(int position) {
        return drawerItems.get(position);
    }

    public int getCount() {
        return drawerItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //Declaramos clase estatica la cual representa un item del menú
    public static class DrawerItemView {
        TextView titulo_itm;
        ImageView icono;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        DrawerItemView view;
        LayoutInflater inflator = activity.getLayoutInflater();
        if (convertView == null) {
            view = new DrawerItemView();
            DrawerItem itm = drawerItems.get(position);
            convertView = inflator.inflate(R.layout.itm, null);
            view.titulo_itm = (TextView) convertView.findViewById(R.id.title_item);
            view.titulo_itm.setText(itm.getTitulo());
            view.icono = (ImageView) convertView.findViewById(R.id.icon);
            view.icono.setImageResource(itm.getIcono());
            convertView.setTag(view);
        } else {
            view = (DrawerItemView) convertView.getTag();
        }
        return convertView;
    }

}