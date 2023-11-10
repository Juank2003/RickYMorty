// FavoriteAdapter.java
package com.example.rickymorty.db;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rickymorty.R;
import com.example.rickymorty.model.Character;

import java.util.ArrayList;
import java.util.List;

// Adaptador para el RecyclerView que muestra los personajes favoritos.
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.CharacterViewHolder> {
    private Context context;
    private List<Character> characterList;

    public FavoriteAdapter(Context context) {
        this.context = context;
        this.characterList = new ArrayList<>(); // Inicializa la lista aquí
    }

    // Método para actualizar los datos del adaptador y refrescar la vista.
    public void setData(List<Character> characterList) {
        this.characterList = characterList;
        notifyDataSetChanged();
    }

    // Infla el layout para cada ítem del RecyclerView.
    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.character_item, parent, false);
        return new CharacterViewHolder(view);
    }

    // Vincula cada personaje a un ViewHolder.
    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        Character character = characterList.get(position);
        holder.characterName.setText(character.getName());



        // Utiliza Glide para cargar la imagen del personaje
        Glide.with(context)
                .load(character.getImage())
                .into(holder.characterImage);


    }

    // Devuelve la cantidad de ítems en el adaptador.
    @Override
    public int getItemCount() {
        return characterList != null ? characterList.size() : 0;
    }

    // ViewHolder que contiene la UI para cada personaje.
    static class CharacterViewHolder extends RecyclerView.ViewHolder {
        TextView characterName;
        TextView characterStatus;
        TextView characterLocation;
        ImageView characterImage;

        CharacterViewHolder(View itemView) {
            super(itemView);
            characterName = itemView.findViewById(R.id.nombre);
            characterStatus = itemView.findViewById(R.id.estado);
            characterLocation = itemView.findViewById(R.id.ubicacion);
            characterImage = itemView.findViewById(R.id.characterImageView);
        }
    }
}