package com.josedlpozo.optimiza;

/**
 * Created by josedlpozo on 12/6/15.
 * <p/>
 * Clase de apoyo para el menu drawer
 */
public class DrawerItem {
    private String titulo;
    private int icono;

    public DrawerItem(String title, int icon) {
        this.titulo = title;
        this.icono = icon;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getIcono() {
        return icono;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }
}