package com.example.rickymorty.datastore;

import android.content.Context;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

public class DataStore {
    private static DataStore instance;
    private RxDataStore<Preferences> dataStore;

    //Constructor privado para el patrón Singleton.
    private DataStore(Context context) {
        if (dataStore == null) {
            dataStore = new RxPreferenceDataStoreBuilder(context, "preferences.pb").build();
        }
    }

    public static synchronized DataStore getInstance(Context context) {
        if (instance == null) {
            instance = new DataStore(context);
        }
        return instance;
    }

    // Método para obtener el DataStore
    public RxDataStore<Preferences> getDataStore() {
        return dataStore;
    }
    public void initialize(Context context, String archivo) {
        if (dataStore == null) {
            dataStore = new RxPreferenceDataStoreBuilder(context, archivo).build();
        }
    }
    // Método para obtener la configuración del tema de forma reactiva
    public Single<Boolean> getThemeSetting() {
        Preferences.Key<Boolean> darkModeKey = PreferencesKeys.booleanKey("dark_mode_key");
        return dataStore.data()
                .map(preferences -> preferences.get(darkModeKey)) // Usar un valor predeterminado
                .first(false); // Si no hay valor, devuelve 'false' por defecto
    }


    /*
      Método para guardar la configuración del tema en DataStore de forma asíncrona.
      Utiliza RxJava para actualizar el valor y manejar el éxito o el error de la operación.
     */
    public void saveThemeSetting(boolean isDarkMode) {
        Preferences.Key<Boolean> darkModeKey = PreferencesKeys.booleanKey("dark_mode_key");
        Disposable disposable = dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutablePreferences = prefs.toMutablePreferences();
            mutablePreferences.set(darkModeKey, isDarkMode);
            return Single.just(mutablePreferences);
        }).subscribe(
                prefsOut -> {},
                throwable -> {}
        );
    }
}