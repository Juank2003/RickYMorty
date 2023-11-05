package com.example.rickymorty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

public class CharacterDetailsActivity extends AppCompatActivity {
    private WebView webView;

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
        html += "<h1>" + character.getName() + "</h1>";
        html += "<p>Status: " + character.getStatus() + "</p>";
        html += "<p>Species: " + character.getSpecies() + "</p>";
        html += "<p>Gender: " + character.getGender() + "</p>";
        html += "<p>Origin: " + character.getLocation().getName() + "</p>";

        // Agrega la etiqueta de imagen para mostrar la imagen del personaje
        html += "<img src='" + character.getImage() + "' alt='" + character.getName() + "' style='max-width:100%;height:auto;'>";

        // Agrega aquí más información si es necesario
        html += "</body></html>";
        return html;
    }
}
