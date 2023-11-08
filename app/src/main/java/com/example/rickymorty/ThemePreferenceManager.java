package com.example.rickymorty;


import android.content.Context;
import android.content.SharedPreferences;

// Gestor de preferencias para guardar y cargar la preferencia de tema oscuro del usuario.
public class ThemePreferenceManager {

    // Nombre del archivo de preferencias donde se guardarán las configuraciones.
    private static final String PREFS_NAME = "theme_preferences";

    // Clave para el valor booleano que indica si el tema oscuro está activo.
    private static final String THEME_KEY = "theme_dark";

    // Objeto SharedPreferences para interactuar con el sistema de preferencias.
    private SharedPreferences sharedPreferences;


    //Constructor de ThemePreferenceManager.
    public ThemePreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    //  Verifica si el tema oscuro está activado.
    public boolean isDarkTheme() {
        // Devuelve el tema actual, falso como predeterminado si no se ha establecido
        return sharedPreferences.getBoolean(THEME_KEY, false);
    }

    //Guarda la preferencia de tema oscuro del usuario.
    public void setDarkTheme(boolean isDarkTheme) {

        // Obtiene un editor para SharedPreferences.
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Pone el valor booleano del tema oscuro en el editor.
        editor.putBoolean(THEME_KEY, isDarkTheme);

        // Aplica los cambios de forma asincrónica
        editor.apply();
    }
}
