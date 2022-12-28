package com.example.tagged.song;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tagged.MainActivity;
import com.example.tagged.R;

import java.util.List;


/**
 * SongAdapter is the bridge between the dataset and the views that host the data.
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder>
{
    private static final String TAG = "SongAdapter";

    private final Context context;
    private final LayoutInflater inflater;
    private List<Song> songData;

    public SongAdapter(Context context)
    {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    /**
     * Sets the dataset to be displayed in the recyclerview.
     */
    public void setSongData(List<Song> songData)
    {
        this.songData = songData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = inflater.inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(itemView, this);
    }

    /**
     * This method handles the writing of data to the holder's view elements from the dataset supplied by setSongData()
     */
    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position)
    {
        Song current = songData.get(position);
        String currentName = current.getTitle();
        Bitmap currentArtwork;
        try
        {
            currentArtwork = BitmapFactory.decodeByteArray(current.getArtwork(), 0, current.getArtwork().length);
        } catch (Exception e)
        {
            Log.w(TAG, "Artwork missing - using a default image from drawable!");
            currentArtwork = BitmapFactory.decodeResource(context.getResources(), R.drawable.artworkmissing);
        }
        holder.songName.setText(currentName);
        holder.songArtwork.setImageBitmap(currentArtwork);
    }

    /**
     * Return the number of songs currently within the dataset on display inside the recyclerview.
     */
    @Override
    public int getItemCount()
    {
        //on first run, before songdata is pulled using ReadDirect class, this will equal null. Returning that would crash the app.
        if (songData != null)
        {
            return songData.size();
        } else
        {
            return 0;
        }
    }

    /**
     * This class handles the inflating of each item within the recyclerview and code to be run based upon user interaction with those items.
     */
    public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public final TextView songName;
        public final ImageView songArtwork;
        final SongAdapter adapter;

        public SongViewHolder(@NonNull View itemView, SongAdapter adapter)
        {
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
            songArtwork = itemView.findViewById(R.id.song_artwork);
            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        /**
         * This class is run when the user clicks on an item inside the recyclerview.
         * A popup menu is inflated, and code is run based upon which item is picked.
         */
        @Override
        public void onClick(View view)
        {
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.inflate(R.menu.itemmenu);
            popupMenu.setOnMenuItemClickListener(item ->
            {
                String filepath = songData.get(getLayoutPosition()).getFilepath();
                switch (item.getItemId())
                {
                    case R.id.view_metadata:

                        ((MainActivity) context).singleView(filepath);
                       return true;
                    case R.id.request_metadata:
                        ((MainActivity) context).metaView(filepath);
                        return true;
                }
                return false;
            });
            popupMenu.show();
        }
    }
}
