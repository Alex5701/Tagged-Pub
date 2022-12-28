package com.example.tagged.database;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.tagged.song.Song;

import java.util.List;

@Dao
public interface SongDao
{
    /**INSERT */
    @Insert
    void insert(Song song);

    /**UPDATE*/
    @Query("UPDATE song_table SET title = :title WHERE filepath = :filepath")
    void updateTitle(String filepath, String title);

    @Query("UPDATE song_table SET artist = :artist WHERE filepath = :filepath")
    void updateArtist(String filepath, String artist);

    @Query("UPDATE song_table SET album = :album WHERE filepath = :filepath")
    void updateAlbum(String filepath, String album);

    @Query("UPDATE song_table SET artwork = :artwork WHERE filepath = :filepath")
    void updateArtwork(String filepath, byte[] artwork);

    @Query("UPDATE song_table SET genre = :genre WHERE filepath = :filepath")
    void updateGenre(String filepath, String genre);

    @Query("UPDATE song_table SET lyrics = :lyrics WHERE filepath = :filepath")
    void updateLyrics(String filepath, String lyrics);

    /**SELECT */
    @Query("Select * from song_table ORDER BY artist ASC")
    LiveData<List<Song>> getAllSongs();

    @Query("SELECT * from song_table WHERE filepath = :filepath")
    Song getSingleSong(String filepath);

    /**DELETE */
    @Query("DELETE FROM song_table")
    void deleteAll();
}
