package com.example.rickymorty;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

public class CharacterDetailsActivity extends AppCompatActivity {
    private WebView webView;
    private FavoriteCharacterDbHelper dbHelper = new FavoriteCharacterDbHelper(CharacterDetailsActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_details);

        // Obtén la información del personaje enviado desde MainActivity
        Character character = (Character) getIntent().getSerializableExtra("character");

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Carga la información del personaje en el WebView
        String htmlData = generateCharacterHtml(character);
        webView.loadData(htmlData, "text/html", "UTF-8");

        // Configura un WebViewClient para abrir enlaces internos en el WebView
        webView.setWebViewClient(new WebViewClient());


        Button addToFavoritesButton = findViewById(R.id.addToFavoriteButton);
        addToFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtén la información del personaje nuevamente
                // Inserta el personaje favorito en la base de datos

                SQLiteDatabase database = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();

                values.put(FavoriteCharacterDbHelper.COLUMN_NAME,character.getId());// Obtén los valores después de configurarlos
                long rowId = database.insert(FavoriteCharacterDbHelper.TABLE_FAVORITE_CHARACTERS, null, values);
                if (rowId != -1) {
                    // Éxito: el personaje se guardó en favoritos
                    Toast.makeText(CharacterDetailsActivity.this, "Personaje guardado en favoritos", Toast.LENGTH_SHORT).show();
                } else {
                    // Error: no se pudo guardar el personaje
                    Log.e("TAG", "Error al guardar el personaje en favoritos");
                    Toast.makeText(CharacterDetailsActivity.this, "Error al guardar el personaje en favoritos", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button removeFromFavoritesButton = findViewById(R.id.removeFromFavoriteButton);
        removeFromFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtén la información del personaje nuevamente
                // Elimina el personaje de favoritos en la base de datos

                SQLiteDatabase database = dbHelper.getWritableDatabase();

                String characterId = String.valueOf(character.getId()); // Obtén el ID del personaje

                // Define la cláusula WHERE para eliminar el personaje por ID
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
        });


        // Busca el botón de compartir y configura el listener
        Button shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtiene la información del personaje nuevamente (puede que no sea necesario)
                Character character = (Character) getIntent().getSerializableExtra("character");

                // Crea un Intent para compartir el enlace del elemento en otras aplicaciones
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Detalles del personaje: " + character.getName());
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Nombre: " + character.getName() + "\n" +
                        "Status: " + character.getStatus() + "\n" +
                        "Species: " + character.getSpecies() + "\n" +
                        "Gender: " + character.getGender() + "\n" +
                        "Origin: " + character.getLocation().getName());

                // Inicia la actividad para compartir
                startActivity(Intent.createChooser(shareIntent, "Compartir detalles del personaje"));
            }
        });
    }

    // Método para generar el contenido HTML que muestra la información del personaje
    private String generateCharacterHtml(Character character) {
        String html = "<html><body>";
        if (character != null) {
            html += "<h1>" + character.getName() + "</h1>";
            html += "<p>Status: " + character.getStatus() + "</p>";
            html += "<p>Species: " + character.getSpecies() + "</p>";
            html += "<p>Gender: " + character.getGender() + "</p>";
            if (character.getLocation() != null) {
                html += "<p>Origin: " + character.getLocation().getName() + "</p>";
            }
            if (!TextUtils.isEmpty(character.getImage())) {
                // Agrega la etiqueta de imagen solo si la URL de la imagen no está vacía
                html += "<img src='" + character.getImage() + "' alt='" + character.getName() + "' style='max-width:100%;height:auto;'>";
            }
            // Agrega aquí más información si es necesario
        }
        html += "</body></html>";
        return html;
    }
}
