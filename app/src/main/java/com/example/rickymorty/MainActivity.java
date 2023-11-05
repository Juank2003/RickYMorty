package com.example.rickymorty;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import com.google.gson.annotations.SerializedName;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CharacterAdapter characterAdapter;
    private Retrofit retrofit;
    private RickAndMortyApi api;

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
        Button viewFavoritesButton = findViewById(R.id.viewFavoritesButton);

        viewFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre FavoriteCharactersActivity al hacer clic en el botón "Ver favoritos"
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
                        characterAdapter.setData(characters);

                        // Obtén la lista de IDs de episodios de todos los personajes
                        List<Integer> episodeIdsList = new ArrayList<>();
                        for (Character character : characters) {
                            List<String> episodeUrls = character.getEpisodeUrls();
                            for (String episodeUrl : episodeUrls) {
                                String[] segments = episodeUrl.split("/");
                                int episodeId = Integer.parseInt(segments[segments.length - 1]);
                                episodeIdsList.add(episodeId);
                            }
                        }

                        // Realiza la solicitud para obtener todos los episodios requeridos
                        String episodeIds = TextUtils.join(",", episodeIdsList); // Convierte la lista de identificadores en una cadena separada por comas
                        Call<JsonArray> episodesCall = api.getEpisodes(episodeIds);
                        episodesCall.enqueue(new Callback<JsonArray>() {
                            @Override
                            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                                if (response.isSuccessful()) {
                                    JsonArray jsonArray = response.body();
                                    List<Episode> episodes = new ArrayList<>();

                                    for (int i = 0; i < jsonArray.size(); i++) {
                                        JsonObject episodeJson = jsonArray.get(i).getAsJsonObject();
                                        Episode episode = new Episode();
                                        episode.setId(episodeJson.get("id").getAsInt());
                                        episode.setName(episodeJson.get("name").getAsString());
                                        episodes.add(episode);
                                    }

                                    // Asigna los episodios a los personajes correspondientes
                                    for (Character character : characters) {
                                        List<String> characterEpisodeUrls = character.getEpisodeUrls();
                                        List<Episode> characterEpisodes = new ArrayList<>();
                                        for (String episodeUrl : characterEpisodeUrls) {
                                            String[] segments = episodeUrl.split("/");
                                            int episodeId = Integer.parseInt(segments[segments.length - 1]);
                                            // Busca el episodio correspondiente por su identificador
                                            for (Episode episode : episodes) {
                                                if (episode.getId() == episodeId) {
                                                    characterEpisodes.add(episode);
                                                }
                                            }
                                        }
                                        character.setEpisodes(characterEpisodes);
                                    }
                                    characterAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonArray> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Error al obtener datos de episodios", Toast.LENGTH_SHORT).show();
                            }
                        });
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
}
