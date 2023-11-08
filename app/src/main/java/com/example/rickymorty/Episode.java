// Episode
package com.example.rickymorty;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

 //Clase modelo para un episodio de "Rick and Morty".
public class Episode implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;

     // Getter y setter para id y nombre
     public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
