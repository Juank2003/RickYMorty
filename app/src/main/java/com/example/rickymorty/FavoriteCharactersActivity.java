package com.example.rickymorty;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavoriteCharactersActivity extends AppCompatActivity {

    private RecyclerView favoritesRecyclerView;
    private FavoriteAdapter favoritesAdapter;
    private List<Character> favoriteCharacterList;
    private List<String> favoriteCharacterNames;
    private FavoriteCharacterDbHelper dbHelper;
    private SQLiteDatabase database;
    private RickAndMortyApi api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_characters);

        dbHelper = new FavoriteCharacterDbHelper(this);
        database = dbHelper.getWritableDatabase();

        favoriteCharacterList = new ArrayList<>();
        favoriteCharacterNames = loadFavoriteCharacters();

        favoritesRecyclerView = findViewById(R.id.recyclerViewFavoriteCharacters);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        favoritesAdapter = new FavoriteAdapter(this);
        favoritesAdapter.setData(favoriteCharacterList);
        favoritesRecyclerView.setAdapter(favoritesAdapter);

        // Configura Retrofit para conectarte a la API de Rick and Morty
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://rickandmortyapi.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(RickAndMortyApi.class);

        loadDetailsForFavoriteCharacters();
    }

    private List<String> loadFavoriteCharacters() {
        List<String> favoriteCharacterNames = loadFavoriteCharacterNames();


        return favoriteCharacterNames;
    }

    private void loadDetailsForFavoriteCharacters() {
        for (final String idCharacter : favoriteCharacterNames) {
            // Construye la URL de la API para obtener los detalles del Pokémon
            String apiUrl = "https://rickandmortyapi.com/api/character/" + idCharacter;
            Character character = new Character();

            Call<Character> call = api.getInfoCharacter(apiUrl);

            call.enqueue(new Callback<Character>() {
                @Override
                public void onResponse(Call<Character> call, Response<Character> response) {
                    if (response.isSuccessful()) {
                        Character detailedCharacters = response.body();

                        character.setName(detailedCharacters.getName());
                        character.setImage(detailedCharacters.getImage());
                        character.setStatus(detailedCharacters.getStatus());
                        character.setLocation(detailedCharacters.getLocation());
                        character.setGender(detailedCharacters.getGender());
                        favoriteCharacterList.add(character);
                        favoritesAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("RickYMortyApp", "Error en onResponse: " + response.message());
                        Toast.makeText(FavoriteCharactersActivity.this, "Error al obtener los detalles del personaje", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Character> call, Throwable t) {
                    Log.e("RickYMortyApp", "Error en onFailure: " + t.getMessage());
                    Toast.makeText(FavoriteCharactersActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private List<String> loadFavoriteCharacterNames() {
        List<String> characterNamesFromDatabase = new ArrayList<>();

        // Abre la base de datos en modo lectura
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // Define una proyección que incluye la columna de nombres (COLUMN_NAME)
        String[] projection = {
                FavoriteCharacterDbHelper.COLUMN_NAME
        };

        // Realiza la consulta
        Cursor cursor = database.query(
                FavoriteCharacterDbHelper.TABLE_FAVORITE_CHARACTERS, // Tabla de la base de datos
                projection, // Columnas que deseas consultar
                null, // Cláusula WHERE (sin filtrar)
                null, // Valores para la cláusula WHERE (sin filtrar)
                null, // No agrupar las filas
                null, // No filtrar por grupo de filas
                null  // No ordenar las filas
        );

        // Recorre el cursor y agrega los nombres de character a la lista
        while (cursor.moveToNext()) {
            String characterName = cursor.getString(cursor.getColumnIndexOrThrow(FavoriteCharacterDbHelper.COLUMN_NAME));
            characterNamesFromDatabase.add(characterName);
        }

        // Cierra el cursor y la base de datos
        cursor.close();
        database.close();

        return characterNamesFromDatabase;
    }
}