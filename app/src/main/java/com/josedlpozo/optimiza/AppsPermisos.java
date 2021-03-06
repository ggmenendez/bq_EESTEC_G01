package com.josedlpozo.optimiza;

import android.graphics.drawable.Drawable;

/**
 * Created by josedlpozo on 16/5/15.
 *
 * Clase de apoyo para las aplicaciones.
 *
 * Necesita los permisos requeridos, su numero, su icono, su nombre y el nombre del paquete
 *
 */
public class AppsPermisos {

    String[] requestedPermissions;
    private int numPermisos;
    private Drawable imagen;
    private String nombre;
    private String nombrePaquete;

    public AppsPermisos(Drawable imagen, String nombre, String[] requestedPermissions, String nombrePaquete) {
        if (requestedPermissions == null) {
            this.requestedPermissions = new String[]{};
            this.numPermisos = 0;
        } else {
            this.requestedPermissions = requestedPermissions;
            this.numPermisos = requestedPermissions.length;
        }
        this.imagen = imagen;
        this.nombre = nombre;
        this.nombrePaquete = nombrePaquete;
    }

    public int getNumPermisos() {
        return numPermisos;
    }

    public Drawable getImagen() {
        return imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public String[] getRequestedPermissions() {
        return requestedPermissions;
    }

    public String getNombrePaquete() {
        return nombrePaquete;
    }
}
