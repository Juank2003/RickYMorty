package com.example.rickymorty;

import com.google.gson.JsonArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RickAndMortyApi {
    @GET("character")
    Call<CharacterList> getCharacters(@Query("page") int page, @Query("per_page") int perPage);

    @GET("episode/{ids}") // Cambiamos el nombre del parámetro a "ids"
    Call<JsonArray> getEpisodes(@Path("ids") String ids); // Cambiamos el tipo de retorno a JsonArray
}
