package com.example.tagged.song;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * This class defines the layout of a song when it is stored to a room database.
 */
@Entity(tableName = "song_table")
public class Song
{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "filepath")
    private final String filepath;
    @ColumnInfo(name = "title")
    private final String title;
    @ColumnInfo(name = "artist")
    private final String artist;
    @ColumnInfo(name = "album")
    private final String album;
    @ColumnInfo(name = "artwork")
    private final byte[] artwork;
    @ColumnInfo(name = "genre")
    private final String genre;
    @ColumnInfo(name = "lyrics")
    private final String lyrics;

    /**
     * Song constructor.
     */
    public Song(@NonNull String filepath, String title, String artist, String album, byte[] artwork, String genre, String lyrics)
    {
        this.filepath = filepath;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.artwork = artwork;
        this.genre = genre;
        this.lyrics = lyrics;
    }

    /**
     * Returns a string containing the absolute filepath the song.
     * NonNull, as this is used as the Primary Key.
     */
    @NonNull
    public String getFilepath()
    {
        return this.filepath;
    }

    /**
     * Returns a string containing the title of the song.
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * Returns a string containing the artist of the song.
     */
    public String getArtist()
    {
        return this.artist;
    }
    /**
     * Returns a string containing the album the song is from.
     */
    public String getAlbum()
    {
        return this.album;
    }
    /**
     * Returns a byte array containing the song artwork.
     * Convert to a Bitmap before attempting to display.
     */
    public byte[] getArtwork()
    {
        return this.artwork;
    }
    /**
     * Returns a string containing the genre of the song.
     */
    public String getGenre()
    {
        return this.genre;
    }
    /**
     * Returns a string containing the lyrics of the song.
     * Use(s) new line character escapes to represent new lines.
     */
    public String getLyrics()
    {
        return this.lyrics;
    }
}
