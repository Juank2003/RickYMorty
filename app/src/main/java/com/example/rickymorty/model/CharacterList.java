// CharacterList.java
package com.example.rickymorty.model;

import com.example.rickymorty.model.Character;
import com.google.gson.annotations.SerializedName;
import java.util.List;

//Clase envoltura para la lista de personajes recibida de la API.
public class CharacterList {
    @SerializedName("results")
    private List<Character> results;

    // Getter para la lista de personajes
    public List<Character> getResults() {
        return results;
    }
}
