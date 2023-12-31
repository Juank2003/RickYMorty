// MainActivity.java
package com.example.rickymorty.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rickymorty.api.ApiClient;
import com.example.rickymorty.api.RickAndMortyApi;
import com.example.rickymorty.model.CharacterAdapter;
import com.example.rickymorty.datastore.DataStore;
import com.example.rickymorty.model.Character;
import com.example.rickymorty.model.CharacterList;
import com.example.rickymorty.model.Episode;
import com.example.rickymorty.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// Muestra una lista de personajes y permite al usuario filtrarlos por estado y cambiar entre un tema claro y oscuro.
public class MainActivity extends AppCompatActivity {

    // Declaraciones de las vistas y adaptadores para la interfaz de usuario
    private RecyclerView recyclerView;
    private CharacterAdapter characterAdapter;

    // Cliente Retrofit y API para hacer llamadas a la API de Rick and Morty
    private Retrofit retrofit;
    private RickAndMortyApi api;

    private Spinner statusSpinner;

    // Variables para manejar la lista de personajes y su filtrado
    private String[] statusOptions = {"All", "Dead", "Alive", "Unknown"};
    private String selectedStatus = "All";
    private List<Character> allCharacters = new ArrayList<>();
    private List<Character> filteredCharacters = new ArrayList<>();
    private DataStore dataStoreSingleton;
    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuración del RecyclerView para mostrar los personajes
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        characterAdapter = new CharacterAdapter(this);
        recyclerView.setAdapter(characterAdapter);

        dataStoreSingleton = DataStore.getInstance(getApplicationContext());

        // Configura Retrofit para conectarte a la API de Rick and Morty
        retrofit = ApiClient.getClient();
        api = retrofit.create(RickAndMortyApi.class);



        // Configuración del spinner para filtrar los personajes por estado
        statusSpinner = findViewById(R.id.statusSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);


        // Configuración del switch para el cambio de tema de la aplicación.
        Switch themeSwitch = findViewById(R.id.themeSwitch);

        // Añadimos una suscripción al CompositeDisposable que observa los cambios en la configuración del tema.
        // Esto se hace mediante la obtención de la configuración de tema actual desde DataStore.
        disposables.add(
                dataStoreSingleton.getThemeSetting() // Obtenemos el valor de la configuración del tema desde DataStore.
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(isDarkTheme -> {
                            // Si se obtiene un valor, actualizamos el estado del switch y aplicamos el tema correspondiente.
                            themeSwitch.setChecked(isDarkTheme);
                            applyTheme(isDarkTheme);
                        }, throwable -> {
                            // En caso de un error al obtener la configuración del tema, se muestra un mensaje.
                            Toast.makeText(MainActivity.this, "Error al cargar la configuración del tema", Toast.LENGTH_SHORT).show();
                        })
        );

        // Configuramos un listener que reacciona cuando el estado del switch cambia (es decir, cuando el usuario lo toca).
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Guardamos la nueva configuración del tema seleccionada por el usuario en DataStore.
            dataStoreSingleton.saveThemeSetting(isChecked);
            // Aplicamos el nuevo tema en tiempo real.
            applyTheme(isChecked);
        });


        // Establece un oyente de selección en el Spinner que permite al usuario filtrar personajes por estado.
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // Este método se llama cada vez que se selecciona un nuevo ítem en el Spinner.
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Obtiene el estado seleccionado del arreglo de opciones basado en la posición seleccionada.
                selectedStatus = statusOptions[position];
                // Filtra la lista de personajes por el estado seleccionado y actualiza la UI.
                filterCharactersByStatus(selectedStatus);
            }

            // Este método se llama cuando nada se ha seleccionado, pero es necesario implementarlo sin acciones.
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No es necesario implementar ninguna acción aquí.
            }
        });

        // Encuentra el botón para ver los personajes favoritos en la vista por su ID.
       ImageButton viewFavoritesButton = findViewById(R.id.viewFavoritesButton);

        // Asigna un OnClickListener al botón que manejará el evento de clic del botón.
        viewFavoritesButton.setOnClickListener(new View.OnClickListener() {
            // Este método se llama cada vez que el botón es presionado.
            @Override
            public void onClick(View v) {
                // Crea un Intent explícito para iniciar la actividad de personajes favoritos.
                Intent intent = new Intent(MainActivity.this, FavoriteCharactersActivity.class);
                // Inicia la actividad especificada en el intent.
                startActivity(intent);
            }
        });

        // Realiza la solicitud a la API para obtener la lista de personajes
        Call<CharacterList> call = api.getCharacters(1, 20);
        // Encola una llamada asincrónica a la API y maneja la respuesta.
        call.enqueue(new Callback<CharacterList>() {
            // Este método se llama automáticamente cuando la API responde a la llamada.
            @Override
            public void onResponse(Call<CharacterList> call, Response<CharacterList> response) {
                // Verifica si la respuesta a la llamada fue exitosa.
                if (response.isSuccessful()) {
                    // Extrae el cuerpo de la respuesta, que es una lista de personajes.
                    CharacterList characterList = response.body();
                    if (characterList != null) {
                        // Almacena la lista de personajes obtenida de la respuesta.
                        List<Character> characters = characterList.getResults();
                        allCharacters = characters;

                        // Prepara una lista para almacenar los IDs de los episodios de cada personaje.
                        List<Integer> episodeIdsList = new ArrayList<>();
                        for (Character character : characters) {
                            // Extrae las URLs de los episodios de cada personaje.
                            List<String> episodeUrls = character.getEpisodeUrls();
                            for (String episodeUrl : episodeUrls) {
                                // Separa las URLs para obtener solo el ID del episodio.
                                String[] segments = episodeUrl.split("/");
                                int episodeId = Integer.parseInt(segments[segments.length - 1]);
                                episodeIdsList.add(episodeId);
                            }
                        }

                        // Une los IDs de episodios con comas para realizar una única llamada a la API.
                        String episodeIds = TextUtils.join(",", episodeIdsList);
                        // Realiza una llamada a la API para obtener los detalles de los episodios.
                        Call<JsonArray> episodesCall = api.getEpisodes(episodeIds);
                        episodesCall.enqueue(new Callback<JsonArray>() {
                            // Manejo de la respuesta para la llamada de los episodios.
                            @Override
                            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                                if (response.isSuccessful()) {
                                    // Extrae el JsonArray de la respuesta.
                                    JsonArray jsonArray = response.body();
                                    List<Episode> episodes = new ArrayList<>();

                                    // Itera a través del JsonArray para obtener los datos de cada episodio.
                                    for (int i = 0; i < jsonArray.size(); i++) {
                                        JsonObject episodeJson = jsonArray.get(i).getAsJsonObject();
                                        Episode episode = new Episode();
                                        episode.setId(episodeJson.get("id").getAsInt());
                                        episode.setName(episodeJson.get("name").getAsString());
                                        episodes.add(episode);
                                    }

                                    // Asigna los episodios a los personajes correspondientes.
                                    for (Character character : characters) {
                                        List<String> characterEpisodeUrls = character.getEpisodeUrls();
                                        List<Episode> characterEpisodes = new ArrayList<>();
                                        for (String episodeUrl : characterEpisodeUrls) {
                                            String[] segments = episodeUrl.split("/");
                                            int episodeId = Integer.parseInt(segments[segments.length - 1]);
                                            // Encuentra y añade los episodios al personaje basado en el ID.
                                            for (Episode episode : episodes) {
                                                if (episode.getId() == episodeId) {
                                                    characterEpisodes.add(episode);
                                                }
                                            }
                                        }
                                        character.setEpisodes(characterEpisodes);
                                    }

                                    // Actualiza la UI para mostrar los personajes filtrados por el estado seleccionado.
                                    filterCharactersByStatus(selectedStatus);
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonArray> call, Throwable t) {
                                // Manejo de errores si la llamada a los episodios falla.
                                Toast.makeText(MainActivity.this, "Error al obtener datos de episodios", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<CharacterList> call, Throwable t) {
                // Manejo de errores si la llamada a la lista de personajes falla.
                Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Aplica el tema oscuro o claro en toda la aplicación en función del parámetro isDarkTheme.
    private void applyTheme(boolean isDarkTheme) {
        int themeMode = isDarkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }

    private void filterCharactersByStatus(String status) {
        // Limpia la lista actual de personajes filtrados para empezar el filtro desde cero.
        filteredCharacters.clear();

        // Si se selecciona "All", agrega todos los personajes a la lista filtrada.
        if (status.equals("All")) {
            filteredCharacters.addAll(allCharacters);
        } else {
            // Si se selecciona otro estado, filtra y agrega solo los personajes que coinciden con ese estado.
            for (Character character : allCharacters) {
                if (character.getStatus().equalsIgnoreCase(status)) {
                    filteredCharacters.add(character);
                }
            }
        }

        // Notifica al adaptador de los cambios en la lista de personajes para que actualice la vista.
        characterAdapter.setData(filteredCharacters);
    }
}
