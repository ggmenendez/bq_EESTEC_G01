package com.josedlpozo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by josedlpozo on 16/5/15.
 * <p/>
 * Base de datos con tablas de aplicacion con permisos, y permisos con descripcion
 */

public class AppsDbHelper extends SQLiteOpenHelper {

    private static int version = 9;

    private static final String DB_NAME = "OCTIMIZA";

    /**
     * Definimos constantes los tipos utilizados
     */
    private static final String TYPE_TEXT = "TEXT";
    private static final String TYPE_INTEGER = "INTEGER";

    /**
     * Definimos constantes nombres de tablas
     */
    private static final String TABLE_NAME = "Permisos_APP";
    private static final String TABLE_NAME_PERMISOS = "PERMISOS";

    /**
     * Definimos constantes con el nombre de las columnas de la tabla permisos_app
     */
    public static final String COLUMNA_ID = "_id";
    public static final String COLUMNA_NOMBRE = "nombre";
    public static final String COLUMNA_PAQUETES = "packages";
    public static final String COLUMNA_IGNORADA = "ignorada";

    /**
     * Definimos constantes con el nombre de las columnas de la tabla permisos
     */
    public static final String COLUMNA_PERMISO = "PERMISO";
    public static final String COLUMNA_DESCRIPCION = "DESCRIPCION";

    private static CursorFactory factory = null;

    /**
     * Definimos constantes las querys para crear tablas
     */
    private static final String CREATE_TABLE_APP_PERMISOS = "CREATE TABLE " + TABLE_NAME + "(" + COLUMNA_ID + " " + TYPE_INTEGER + " PRIMARY KEY," + COLUMNA_NOMBRE + " " + TYPE_TEXT + " NOT NULL, " +
            COLUMNA_PAQUETES + " " + TYPE_TEXT + " NOT NULL," + COLUMNA_IGNORADA + " " + TYPE_INTEGER + ")";

    private static final String CREATE_TABLE_PERMISOS = "CREATE TABLE " + TABLE_NAME_PERMISOS + "(" + COLUMNA_ID + " " + TYPE_INTEGER + " PRIMARY KEY, " + COLUMNA_PERMISO + " " + TYPE_TEXT + " NOT NULL, " + COLUMNA_DESCRIPCION + " " + TYPE_TEXT + " NOT NULL)";


    public AppsDbHelper(Context context) {
        super(context, DB_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * Creación de base de datos
         */
        db.execSQL(CREATE_TABLE_APP_PERMISOS);
        db.execSQL(CREATE_TABLE_PERMISOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /**
         * Eliminamos base de datos anteriores y actualizamos versión
         */
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PERMISOS);
        onCreate(db);
    }

}