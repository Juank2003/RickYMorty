// Character.java
package com.example.rickymorty;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Character implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    private String status;
    private String species;
    private String image;
    private Location location;
    private String gender;  // Agregamos el atributo gender
    @SerializedName("episode")
    private List<String> episodeUrls;
    private List<Episode> episodes;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // Resto de los métodos de la clase
    // ...

    public List<String> getEpisodeUrls() {
        return episodeUrls;
    }

    public void setEpisodeUrls(List<String> episodeUrls) {
        this.episodeUrls = episodeUrls;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}