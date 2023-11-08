package com.example.rickymorty;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CharacterDetailsActivity extends AppCompatActivity {
    private WebView webView;
    private FavoriteCharacterDbHelper dbHelper = new FavoriteCharacterDbHelper(CharacterDetailsActivity.this);
    private CheckBox favoriteCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_details);

        // Obtén la información del personaje enviado desde MainActivity
        final Character character = (Character) getIntent().getSerializableExtra("character");

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Carga la información del personaje en el WebView
        String htmlData = generateCharacterHtml(character);
        webView.loadData(htmlData, "text/html", "UTF-8");

        // Configura un WebViewClient para abrir enlaces internos en el WebView
        webView.setWebViewClient(new WebViewClient());

        favoriteCheckBox = findViewById(R.id.favoriteCheckBox);
        favoriteCheckBox.setChecked(isCharacterInFavorites(character.getId()));

        favoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Inserta el personaje favorito en la base de datos
                    SQLiteDatabase database = dbHelper.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(FavoriteCharacterDbHelper.COLUMN_NAME, character.getId());

                    long rowId = database.insert(FavoriteCharacterDbHelper.TABLE_FAVORITE_CHARACTERS, null, values);

                    if (rowId != -1) {
                        // Éxito: el personaje se guardó en favoritos
                        Toast.makeText(CharacterDetailsActivity.this, "Personaje guardado en favoritos", Toast.LENGTH_SHORT).show();
                    } else {
                        // Error: no se pudo guardar el personaje
                        Log.e("TAG", "Error al guardar el personaje en favoritos");
                        Toast.makeText(CharacterDetailsActivity.this, "Error al guardar el personaje en favoritos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Elimina el personaje de favoritos en la base de datos
                    SQLiteDatabase database = dbHelper.getWritableDatabase();

                    String characterId = String.valueOf(character.getId());

                    String selection = FavoriteCharacterDbHelper.COLUMN_NAME + " = ?";
                    String[] selectionArgs = { characterId };

                    int deletedRows = database.delete(FavoriteCharacterDbHelper.TABLE_FAVORITE_CHARACTERS, selection, selectionArgs);

                    if (deletedRows > 0) {
                        // Éxito: el personaje se eliminó de favoritos
                        Toast.makeText(CharacterDetailsActivity.this, "Personaje eliminado de favoritos", Toast.LENGTH_SHORT).show();
                    } else {
                        // Error: no se pudo eliminar el personaje
                        Log.e("TAG", "Error al eliminar el personaje de favoritos");
                        Toast.makeText(CharacterDetailsActivity.this, "Error al eliminar el personaje de favoritos", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Resto del código...
    }

    private boolean isCharacterInFavorites(int characterId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String selection = FavoriteCharacterDbHelper.COLUMN_NAME + " = ?";
        String[] selectionArgs = { String.valueOf(characterId) };

        Cursor cursor = database.query(
                FavoriteCharacterDbHelper.TABLE_FAVORITE_CHARACTERS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean isCharacterInFavorites = cursor.getCount() > 0;

        cursor.close();
        database.close();

        return isCharacterInFavorites;
    }

    private String generateCharacterHtml(Character character) {
        String html = "<html><body>";
        if (character != null) {
            html += "<h1>" + character.getName() + "</h1>";
            html += "<p>Status: " + character.getStatus() + "</p>";
            html += "<p>Species: " + character.getSpecies() + "</p>";
            html += "<p>Gender: " + character.getGender() + "</p>";
            if (character.getOrigin() != null) {
                html += "<p>Origin: " + character.getOrigin().getName() + "</p>";
            }
            if (character.getLocation() != null) {
                html += "<p>Location: " + character.getLocation().getName() + "</p>";
            }
            if (!TextUtils.isEmpty(character.getImage())) {
                // Agrega la etiqueta de imagen solo si la URL de la imagen no está vacía
                html += "<img src='" + character.getImage() + "' alt='" + character.getName() + "' style='max-width:100%;height:auto;'>";
            }
        }
        html += "</body></html>";
        return html;
    }

}