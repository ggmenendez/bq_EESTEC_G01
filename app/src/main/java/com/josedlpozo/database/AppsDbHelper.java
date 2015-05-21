package com.josedlpozo.database;

/**
 * Created by josedlpozo on 16/5/15.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class AppsDbHelper extends SQLiteOpenHelper {

    private static int version = 5;
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

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMNA_ID + " INTEGER PRIMARY KEY," + COLUMNA_NOMBRE + " " + TYPE_TEXT + " NOT NULL, " +
            COLUMNA_PAQUETES + " " + TYPE_TEXT + " NOT NULL," + COLUMNA_PERMISOS + " " + TYPE_TEXT + ", " + COLUMNA_NUM_PERMISOS + " " + TYPE_INTEGER + ")";

    private String[] columnas = new String[]{COLUMNA_ID, COLUMNA_NOMBRE, COLUMNA_PAQUETES, COLUMNA_PERMISOS, COLUMNA_NUM_PERMISOS};
    ;

    public AppsDbHelper(Context context) {
        super(context, TABLE_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(this.getClass().toString(), "Creando base de datos");

        db.execSQL(CREATE_TABLE);


        Log.i(this.getClass().toString(), "Base de datos creada");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public byte[] insertImage(Drawable dbDrawable) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Bitmap bitmap = ((BitmapDrawable) dbDrawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream);
        return stream.toByteArray();
    }

    public ImageHelper getImage(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor2 = db.query(TABLE_NAME,
                columnas,
                "_id" + " LIKE '" + id + "%'", null, null, null, null);
        ImageHelper imageHelper = new ImageHelper();
        if (cursor2.moveToFirst()) {
            do {
                imageHelper.setImageId(cursor2.getString(1));
                imageHelper.setImageByteArray(cursor2.getBlob(2));
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        db.close();
        return imageHelper;
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