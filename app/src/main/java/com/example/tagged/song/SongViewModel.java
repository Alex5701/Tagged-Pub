package com.example.tagged.song;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.tagged.database.SongRepository;
import java.util.List;

/**
 * ViewModel that holds and provides data to UI elements (SongAdapter and ViewFragment).
 * Also provides access to the repository for updating songs and deleting entries.
 */

public class SongViewModel extends AndroidViewModel
{
    private final SongRepository songRepository;
    private final LiveData<List<Song>> allSongs;

    public SongViewModel(@NonNull Application application)
    {
        super(application);
        songRepository = new SongRepository(application);
        allSongs = songRepository.getAllSongs();
    }

    public LiveData<List<Song>> getAllSongs()
    {
        return allSongs;
    }

    public Song getSong(String filepath)
    {
        return songRepository.getSingleSong(filepath);
    }

    public void deleteAll()
    {
        songRepository.deleteAll();
    }

    public void insert(Song song)
    {
        songRepository.insertSong(song);
    }

    public void updateTitle(String filepath, String title)
    {
        songRepository.updateTitle(filepath, title);
    }

    public void updateArtist(String filepath, String artist)
    {
        songRepository.updateArtist(filepath, artist);
    }

    public void updateAlbum(String filepath, String album)
    {
        songRepository.updateAlbum(filepath, album);
    }

    public void updateArtwork(String filepath, byte[] artwork)
    {
        songRepository.updateArtwork(filepath, artwork);
    }

    public void updateGenre(String filepath, String genre)
    {
        songRepository.updateGenre(filepath, genre);
    }

    public void updateLyrics(String filepath, String lyrics)
    {
        songRepository.updateLyrics(filepath, lyrics);
    }

}
