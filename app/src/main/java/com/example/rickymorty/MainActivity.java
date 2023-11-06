package com.example.rickymorty;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CharacterAdapter characterAdapter;
    private Retrofit retrofit;
    private RickAndMortyApi api;
    private Spinner statusSpinner;

    private String[] statusOptions = {"All", "Dead", "Alive", "Unknown"};
    private String selectedStatus = "All";
    private List<Character> allCharacters = new ArrayList<>();
    private List<Character> filteredCharacters = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        characterAdapter = new CharacterAdapter(this);
        recyclerView.setAdapter(characterAdapter);

        // Configura Retrofit para conectarte a la API de Rick and Morty
        retrofit = new Retrofit.Builder()
                .baseUrl("https://rickandmortyapi.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(RickAndMortyApi.class);

        statusSpinner = findViewById(R.id.statusSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedStatus = statusOptions[position];
                filterCharactersByStatus(selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No es necesario hacer nada aquí
            }
        });

        Button viewFavoritesButton = findViewById(R.id.viewFavoritesButton);

        viewFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FavoriteCharactersActivity.class);
                startActivity(intent);
            }
        });

        // Realiza la solicitud a la API para obtener la lista de personajes
        Call<CharacterList> call = api.getCharacters(1, 20);
        call.enqueue(new Callback<CharacterList>() {
            @Override
            public void onResponse(Call<CharacterList> call, Response<CharacterList> response) {
                if (response.isSuccessful()) {
                    CharacterList characterList = response.body();
                    if (characterList != null) {
                        List<Character> characters = characterList.getResults();
                        allCharacters = characters;

                        filterCharactersByStatus(selectedStatus);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error al obtener datos de la API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CharacterList> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterCharactersByStatus(String status) {
        filteredCharacters.clear();

        if (status.equals("All")) {
            filteredCharacters.addAll(allCharacters);
        } else {
            for (Character character : allCharacters) {
                if (character.getStatus().equalsIgnoreCase(status)) {
                    filteredCharacters.add(character);
                }
            }
        }

        characterAdapter.setData(filteredCharacters);
    }
}
