// Location.java
package com.example.rickymorty;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

//Clase modelo para la ubicación de un personaje.
public class Location implements Serializable {
    @SerializedName("name")
    private String name;

    // Getter y setter para el nombre de la ubicación
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
