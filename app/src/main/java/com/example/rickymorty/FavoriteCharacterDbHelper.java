package com.example.rickymorty;
//Igual que eloy
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteCharacterDbHelper extends SQLiteOpenHelper {
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

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE_CHARACTERS);
        onCreate(db);
    }
}
