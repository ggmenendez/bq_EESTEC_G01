package com.josedlpozo.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by josedlpozo on 16/5/15.
 * <p/>
 * Clase de ayuda para la base de datos
 */


public class PermisosAdapter {

    /**
     * Definimos constante con el nombre de la tabla
     */
    public static final String C_TABLA = "PERMISOS";

    /**
     * Definimos constantes con el nombre de las columnas de la tabla
     */
    public static final String COLUMNA_ID = "_ID";
    public static final String COLUMNA_PERMISO = "PERMISO";
    public static final String COLUMNA_DESCRIPCION = "DESCRIPCION";


    private Context contexto;
    private AppsDbHelper dbHelper;
    private SQLiteDatabase db;

    /**
     * Definimos lista de columnas de la tabla para utilizarla en las consultas a la base de datos
     */
    private String[] columnas = new String[]{COLUMNA_ID, COLUMNA_PERMISO, COLUMNA_DESCRIPCION};

    public PermisosAdapter(Context context) {
        this.contexto = context;
    }

    public PermisosAdapter abrir() throws SQLException {
        dbHelper = new AppsDbHelper(contexto);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void cerrar() {
        dbHelper.close();
    }

    /**
     * Devuelve cursor con todos las columnas de la tabla
     */
    public Cursor getCursor() throws SQLException {
        Cursor c = db.query(true, C_TABLA, columnas, null, null, null, null, null, null);

        return c;
    }

    /**
     * Inserta los valores en un registro de la tabla
     */
    public long insert(ContentValues reg) {
        if (db == null)
            abrir();

        return db.insert(C_TABLA, null, reg);
    }
}