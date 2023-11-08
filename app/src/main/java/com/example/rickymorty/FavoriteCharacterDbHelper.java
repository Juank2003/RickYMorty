//FavoriteCharacterDbHelper
package com.example.rickymorty;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Clase de ayuda para manejar la creación y gestión de la base de datos de personajes favoritos.
public class FavoriteCharacterDbHelper extends SQLiteOpenHelper {
    // Constantes para el nombre de la base de datos, versión y estructura de la tabla.
    private static final String DATABASE_NAME = "FavoriteCharacters.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_FAVORITE_CHARACTERS = "favorite_characters";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";

    private static final String DATABASE_CREATE = "create table " +
            TABLE_FAVORITE_CHARACTERS + " (" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_NAME + " text);";

    public FavoriteCharacterDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Se llama al crear la base de datos. Aquí se define la estructura de la tabla.
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    // Se llama cuando se actualiza la base de datos a una versión más reciente.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE_CHARACTERS);
        onCreate(db);
    }
}
