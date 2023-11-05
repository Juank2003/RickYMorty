// CharacterList.java
package com.example.rickymorty;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CharacterList {
    @SerializedName("results")
    private List<Character> results;

    public List<Character> getResults() {
        return results;
    }
}
