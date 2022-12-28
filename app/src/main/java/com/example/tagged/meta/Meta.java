package com.example.tagged.meta;

import android.graphics.Bitmap;


/**
 * This class contains the standard layout of a Meta set that is to be displayed to the user after requesting it from MusicBrainz and CoverArtArchive.
 */
public class Meta
{
    private final String title;
    private final String artist;
    private final String album;
    private final String genre;
    private final String releaseID;
    private Bitmap coverart;


    /**
     *Meta constructor. To be initialised when pulling releases from MusicBrainz.
     */
    public Meta(String title, String artist, String album, String genre, String releaseID)
    {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.releaseID = releaseID;
    }

    /**
     * Return the title of the track.
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * Return the artist of the track.
     */
    public String getArtist()
    {
        return this.artist;
    }

    /**
     * Return the album name the track features on. Will vary between releases.
     */
    public String getAlbum()
    {
        return this.album;
    }

    /**
     * Return the genre of the track.
     */
    public String getGenre()
    {
        return this.genre;
    }

    /**
     * Return the releaseID. Will vary between releases.
     */
    public String getReleaseID()
    {
        return this.releaseID;
    }

    /**
     * Return the artwork of the release. Will vary between releases.
     */
    public Bitmap getArtwork()
    {
        return this.coverart;
    }

    /**
     * Set the artwork of the release. Will vary between releases.
     * Should be called after pulling the artwork from CoverArtArchive.
     */
    public void setArtwork(Bitmap coverart)
    {
        this.coverart = coverart;
    }

}
