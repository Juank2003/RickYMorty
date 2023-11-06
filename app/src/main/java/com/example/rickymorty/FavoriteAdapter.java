package com.example.rickymorty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.CharacterViewHolder> {
    private Context context;
    private List<Character> characterList;

    public FavoriteAdapter(Context context) {
        this.context = context;
        this.characterList = new ArrayList<>(); // Inicializa la lista aquí
    }

    public void setData(List<Character> characterList) {
        this.characterList = characterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.character_item, parent, false);
        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        Character character = characterList.get(position);
        holder.characterName.setText(character.getName());


        // Utiliza Glide para cargar la imagen del personaje
        Glide.with(context)
                .load(character.getImage())
                .into(holder.characterImage);


    }

    @Override
    public int getItemCount() {
        return characterList != null ? characterList.size() : 0;
    }

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