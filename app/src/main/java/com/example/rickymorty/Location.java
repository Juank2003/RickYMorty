// Location.java
package com.example.rickymorty;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Location implements Serializable {
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
