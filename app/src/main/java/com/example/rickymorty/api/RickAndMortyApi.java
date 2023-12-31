// RickAndMortyApi.java
package com.example.rickymorty.api;

import com.example.rickymorty.model.Character;
import com.example.rickymorty.model.CharacterList;
import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

// Interfaz de Retrofit que define los métodos para acceder a las direcciones url de la API de Rick and Morty.
//Usa Gson para deserializar las respuestas JSON de la API.
public interface RickAndMortyApi {

    // Obtiene una lista de personajes con paginación.
    @GET("character")
    Call<CharacterList> getCharacters(@Query("page") int page, @Query("per_page") int perPage);

    // Obtiene detalles de varios episodios pasando sus IDs.
    @GET("episode/{ids}")
    Call<JsonArray> getEpisodes(@Path("ids") String ids);

    // Obtiene información detallada de un personaje por una URL específica.
    @GET
    Call<Character> getInfoCharacter(@Url String url);
}
