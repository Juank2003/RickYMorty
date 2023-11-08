// CharacterAdapter.java
package com.example.rickymorty;
import android.content.Context;
import android.content.Intent;
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

// Adaptador para el RecyclerView que maneja la visualización de personajes.
public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder> {
    private Context context;
    private List<Character> characterList;

    // Constructor del adaptador
    public CharacterAdapter(Context context) {
        this.context = context;
        this.characterList = new ArrayList<>();
    }

    // Establece los datos del personaje y notifica al adaptador para refrescar la lista
    public void setData(List<Character> characterList) {
        this.characterList = characterList;
        notifyDataSetChanged();
    }

    // Crea nuevas vistas (invocadas por el layout manager)
    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.character_item, parent, false);
        return new CharacterViewHolder(view);
    }

    // Reemplaza el contenido de una vista (invocado por el layout manager)
    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        Character character = characterList.get(position);
        holder.characterName.setText(character.getName());
        holder.characterStatus.setText(character.getStatus());
        holder.characterLocation.setText("Location: " + character.getLocation().getName());

        // Obtener el nombre del primer episodio
        List<Episode> episodes = character.getEpisodes();
        if (episodes != null && !episodes.isEmpty()) {
            Episode firstEpisode = episodes.get(0);
            holder.characterEpisode.setText("Episode: " + firstEpisode.getName());
        } else {
            holder.characterEpisode.setText("Episode: N/A");
        }

        // Utiliza Glide para cargar la imagen del personaje
        Glide.with(context)
                .load(character.getImage())
                .into(holder.characterImage);

        // Configura un listener para abrir una actividad detallada cuando se toca un personaje
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Configura un Intent para abrir CharacterDetailsActivity
                Intent intent = new Intent(context, CharacterDetailsActivity.class);
                // Envía el objeto Character al Activity
                intent.putExtra("character", character);
                context.startActivity(intent);
            }
        });
    }

    // Devuelve el tamaño de la lista de datos (invocado por el layout manager)
    @Override
    public int getItemCount() {
        return characterList != null ? characterList.size() : 0;
    }

    // Proporciona una referencia a las vistas para cada ítem de
    static class CharacterViewHolder extends RecyclerView.ViewHolder {
        TextView characterName;
        TextView characterStatus;
        TextView characterLocation;
        TextView characterEpisode;
        ImageView characterImage;

        // Constructor del ViewHolder para inicializar la UI
        CharacterViewHolder(View itemView) {
            super(itemView);
            characterName = itemView.findViewById(R.id.nombre);
            characterStatus = itemView.findViewById(R.id.estado);
            characterLocation = itemView.findViewById(R.id.ubicacion);
            characterEpisode = itemView.findViewById(R.id.episodio);
            characterImage = itemView.findViewById(R.id.characterImageView);
        }
    }
}
