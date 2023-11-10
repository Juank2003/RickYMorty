// ApiClient.java
package com.example.rickymorty.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//Clase que crea una instancia de Retrofit.

public class ApiClient {
    private static final String BASE_URL = "https://rickandmortyapi.com/api/";

    // Variable estática para mantener la referencia de Retrofit
    private static Retrofit retrofit;

    //metodo que inicializa la instancia de Retrofit y si existe devuelve la que ya existe
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // Establece la URL base de la API
                    .addConverterFactory(GsonConverterFactory.create()) // Agrega el convertidor Gson para la serialización
                    .build(); // Construye la instancia de Retrofit
        }
        return retrofit;
    }
}
