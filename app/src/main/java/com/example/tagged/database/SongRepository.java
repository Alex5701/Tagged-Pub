package com.example.tagged.database;

import android.app.Application;

import androidx.lifecycle.LiveData;


import com.example.tagged.song.Song;

import java.util.List;

/**
 * Single source of truth for accessing the database.
 * Currently only one ViewModel accessing this class.
 */
public class SongRepository
{
    private final SongDao songDao;
    private final LiveData<List<Song>> songList;


   public SongRepository(Application application)
    {
        SongRoomDatabase database = SongRoomDatabase.getDatabase(application);
        songDao = database.songDao();
        songList = songDao.getAllSongs();
    }

    public LiveData<List<Song>> getAllSongs()
    {
        return songList;
    }

    public Song getSingleSong(String filepath)
    {
       return songDao.getSingleSong(filepath);
    }

    public void deleteAll()
    {
        songDao.deleteAll();
    }

    public void insertSong(Song song)
    {
        new insertThread(songDao, song).start();
    }

    public void updateTitle(String filepath, String title)
    {
        songDao.updateTitle(filepath, title);
    }

    public void updateArtist(String filepath, String artist)
    {
        songDao.updateArtist(filepath, artist);
    }

    public void updateAlbum(String filepath, String album)
    {
        songDao.updateAlbum(filepath, album);
    }

    public void updateArtwork(String filepath, byte[] artwork)
    {
        songDao.updateArtwork(filepath, artwork);
    }

    public void updateGenre(String filepath, String genre)
    {
        songDao.updateGenre(filepath, genre);
    }

    public void updateLyrics(String filepath, String lyrics)
    {
        songDao.updateLyrics(filepath, lyrics);
    }

    static class insertThread extends Thread
    {
        private final SongDao songDao;
        private final Song song;

        insertThread(SongDao syncDao, Song song)
        {
            this.songDao = syncDao;
            this.song = song;
        }

        @Override
        public void run()
        {
            songDao.insert(song);
        }
    }
}
