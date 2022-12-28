package com.example.tagged;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.example.tagged.song.Song;
import com.example.tagged.song.SongAdapter;
import com.example.tagged.song.SongFragment;
import com.example.tagged.song.SongViewModel;
import com.example.tagged.meta.MetaFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity of the app. Handles the creation of default views, UI elements and the associated functions plus data with those elements where appropriate.
 */
public class MainActivity extends AppCompatActivity
{
    public SongViewModel songViewModel;
    public RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        //get the recycler view
        rv = findViewById(R.id.recyclesong);
        SongAdapter sAdapter = new SongAdapter(this);
        rv.setAdapter(sAdapter);
        //give a linear layout manager
        rv.setLayoutManager(new LinearLayoutManager(this));
        final Observer<List<Song>> songObserver = sAdapter::setSongData; //observe for changes - on change, re-set the song data.
        songViewModel.getAllSongs().observe(this, songObserver);
    }



    /**
     * Creates a new ViewFragment detailing the metadata associated with the given string filepath.
     */
    public void singleView(String filepath)
    {
        SongFragment sf = SongFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("filepath", filepath);
        sf.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_view, sf).addToBackStack(null).commit();
    }

    /**
     * Creates a new MetaFragment, passes a filepath to the fragment.
     */
    public void metaView(String filepath)
    {
        MetaFragment mf = MetaFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("filepath", filepath);
        mf.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_view, mf).addToBackStack(null).commit();
    }


    /**
     * Overwrite the menu to inflate our custom menu layout.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Calls ReadDirect to get information of tracks on device and then requests SongViewModel to insert into the Room database.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Thread thread = new Thread(() ->
        {
            songViewModel.deleteAll();
            ReadDirect library = new ReadDirect(this);

            ArrayList<String> paths = library.getPath();
            ArrayList<String> titles = library.getTitle();
            ArrayList<String> artists = library.getArtist();
            ArrayList<String> albums = library.getAlbum();
            ArrayList<byte[]> artwork = library.getArtwork();
            ArrayList<String> genre = library.getGenre();
            ArrayList<String> lyrics = library.getLyrics();
            for (int i = 0; i < paths.size(); i++)
            {
                Song song = new Song((paths.get(i)), titles.get(i), artists.get(i), albums.get(i), artwork.get(i), genre.get(i), lyrics.get(i));
                songViewModel.insert(song);
            }
        });
        thread.start();
        return true;
    }


}

