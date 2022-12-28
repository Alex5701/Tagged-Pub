package com.example.tagged.meta;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tagged.R;

import java.util.List;

/**
 * MetaAdapter handles displaying the Meta set to the user via a custom Viewholder.
  */
public class MetaAdapter extends RecyclerView.Adapter<MetaAdapter.MetaViewHolder>
{
    private final LayoutInflater inflater;
    private List<Meta> metadata;
    private final MetaAdapterInterface metaInterface;

    public MetaAdapter(Context context, MetaAdapterInterface metaInterface)
    {
        this.inflater = LayoutInflater.from(context);
        this.metaInterface = metaInterface;
    }


    /**
     * Sets the dataset to be displayed in the recyclerview.
     */
   public void setMetadata(List<Meta> metadata)
    {
        this.metadata = metadata;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = inflater.inflate(R.layout.item_meta, parent, false);
        return new MetaViewHolder(itemView, this);
    }


    /**
     * Link each metadata item with the relating View defined in the ViewHolder.
     * Sets an onclicklistener that calls the interface method when a metaset is selected.
     */
    @Override
    public void onBindViewHolder(@NonNull MetaViewHolder holder, int position)
    {
        Bitmap artwork = metadata.get(position).getArtwork();
        String title = metadata.get(position).getTitle();
        String artist = metadata.get(position).getArtist();
        String album = metadata.get(position).getAlbum();
        String genre =  metadata.get(position).getGenre();
        holder.header.setText("item " + String.valueOf((position + 1)));
        holder.artwork.setImageBitmap(artwork);
        holder.title.setText("Title: " + title);
        holder.artist.setText("Artist: " + artist);
        holder.album.setText("Album: " + album);
        holder.genre.setText("Genre: " + genre);
        holder.saveButton.setOnClickListener(view -> metaInterface.OnItemClicked(holder.getAdapterPosition()));
    }

    /**
     * Returns total number of Meta sets on display.
     */
    @Override
    public int getItemCount()
    {
        if (metadata != null)
        {
            return metadata.size();
        }
        else
        {
            return 0;
        }
    }

    /**
     * Interface that enables the handling of click events to be defined in the calling class.
     */
    public interface MetaAdapterInterface
    {
        void OnItemClicked(int id);
    }


    /**
     * This class handles the inflating of views that display Meta sets in the recyclerview.
     */
    public static class MetaViewHolder extends RecyclerView.ViewHolder
    {
        public final TextView header;
        public final ImageView artwork;
        public final TextView title;
        private final TextView artist;
        private final TextView album;
        private final TextView genre;
        private final Button saveButton;
        final MetaAdapter adapter;

        public MetaViewHolder(@NonNull View itemView, MetaAdapter adapter)
        {
            super(itemView);
            header = itemView.findViewById(R.id.header);
            artwork = itemView.findViewById(R.id.artwork);
            title = itemView.findViewById(R.id.title);
            artist = itemView.findViewById(R.id.artist);
            album = itemView.findViewById(R.id.album);
            genre = itemView.findViewById(R.id.genre);
            saveButton = itemView.findViewById(R.id.savebutton);
            this.adapter = adapter;
        }

    }
}
