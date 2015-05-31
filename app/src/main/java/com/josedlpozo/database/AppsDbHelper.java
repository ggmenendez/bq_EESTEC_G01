package com.josedlpozo.database;

/**
 * Created by josedlpozo on 16/5/15.
 */


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AppsDbHelper extends SQLiteOpenHelper {

    private static int version = 8;

    private static final String DB_NAME = "OCTIMIZA";

    private static final String TABLE_NAME = "Permisos_APP";
    private static final String TYPE_TEXT = "TEXT";
    private static final String TYPE_INTEGER = "INTEGER";

    /**
     * Definimos constantes con el nombre de las columnas de la tabla
     */
    public static final String COLUMNA_ID = "_id";
    public static final String COLUMNA_NOMBRE = "nombre";
    public static final String COLUMNA_PAQUETES = "packages";
    public static final String COLUMNA_PERMISOS = "permisos";
    public static final String COLUMNA_NUM_PERMISOS = "num_permisos";


    private static CursorFactory factory = null;

    private static final String CREATE_TABLE_APP_PERMISOS = "CREATE TABLE " + TABLE_NAME + "(" + COLUMNA_ID + " INTEGER PRIMARY KEY," + COLUMNA_NOMBRE + " " + TYPE_TEXT + " NOT NULL, " +
            COLUMNA_PAQUETES + " " + TYPE_TEXT + " NOT NULL)";

    private static final String CREATE_TABLE_PERMISOS = "CREATE TABLE PERMISOS (_ID INTEGER PRIMARY KEY, PERMISO TEXT NOT NULL, DESCRIPCION TEXT NOT NULL)";

    private String[] columnas = new String[]{COLUMNA_ID, COLUMNA_NOMBRE, COLUMNA_PAQUETES, COLUMNA_PERMISOS, COLUMNA_NUM_PERMISOS};
    ;

    public AppsDbHelper(Context context) {
        super(context, DB_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(this.getClass().toString(), "Creando base de datos");
        db.execSQL(CREATE_TABLE_APP_PERMISOS);
        db.execSQL(CREATE_TABLE_PERMISOS);
        Log.i(this.getClass().toString(), "Base de datos creada");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS PERMISOS");
        onCreate(db);
    }

    public static String strSeparator = "__,__";

    public static String convertArrayToString(String[] array) {
        String str = "";
        if (array == null) {
            return str;
        }
        for (int i = 0; i < array.length; i++) {
            str = str + array[i];
            // Do not append comma at the end of last element
            if (i < array.length - 1) {
                str = str + strSeparator;
            }
        }
        return str;
    }

    public static String[] convertStringToArray(String str) {
        String[] arr = str.split(strSeparator);
        return arr;
    }

}