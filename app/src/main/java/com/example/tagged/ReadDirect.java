package com.example.tagged;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.FilenameUtils;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * This class and it's inner class, FileHandler are responsible for pulling audio files recursively starting from a root and extracting existing metadata from those files.
 * Access the pulled metadata using the Getter methods.
 */
public class ReadDirect
{
    private final Context context;
    private final ArrayList<String> path = new ArrayList<>();
    private final ArrayList<String> title = new ArrayList<>();
    private final ArrayList<String> artist = new ArrayList<>();
    private final ArrayList<String> album = new ArrayList<>();
    private final ArrayList<byte[]> artwork = new ArrayList<>();
    private final ArrayList<String> genre  = new ArrayList<>();
    private final ArrayList<String> lyrics = new ArrayList<>();

    /**
     * Constructor takes context (for pulling R.drawable.artworkmissing) and calls read();
     */
    public ReadDirect(Context context)
    {
        this.context = context;
        try
        {
            read("/sdcard/Music");
        } catch (IOException | CannotReadException | TagException | InvalidAudioFrameException | ReadOnlyFileException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Calls inner-class FilerHandler to parse a given rootpath string argument.
     * Then this method gets metadata from all audio files returned by FilerHandler's purge() method.
     * If metadata is missing, a replacement value is given indicating that the given metadata item is unavailable.
     */
    public void read(String rootPath) throws IOException, CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException
    {
        File root = new File(rootPath);
        FileHandler fw = new FileHandler();
        fw.walk(root);
        List<String> absoluteFiles = fw.purge();
        for (String filePath : absoluteFiles)
        {
            AudioFile f = AudioFileIO.read(new File(filePath));
            Tag tag = f.getTag();
            addPath(filePath);
            addTitle(tag.getFirst(FieldKey.TITLE));
            addArtist(tag.getFirst(FieldKey.ARTIST));
            addAlbum(tag.getFirst(FieldKey.ALBUM));
            addArtwork(tag.getFirstArtwork());
            addGenre(tag.getFirst(FieldKey.GENRE));
            addLyrics(tag.getFirst(FieldKey.LYRICS));
        }
    }


    void addPath(String filePath)
    {
        path.add(filePath);
    }

    void addTitle(String title)
    {
        if (title.equals(""))
        {
            this.title.add("TITLE_MISSING");
        }
        else
        {
            this.title.add(title);
        }
    }

    void addArtist(String artist)
    {
        if (artist.equals(""))
        {
            this.artist.add("ARTIST_MISSING");
        }
        else
        {
            this.artist.add(artist);
        }
    }

    void addAlbum(String album)
    {
        if (album.equals(""))
        {
            this.album.add("ALBUM_MISSING");
        }
        else
        {
            this.album.add(album);
        }
    }

    void addArtwork(Artwork artwork)
    {
        if (artwork == null)
        {
            Bitmap b = BitmapFactory.decodeResource(context.getResources(),R.drawable.artworkmissing);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 100, stream);
            this.artwork.add(stream.toByteArray());
        }
        else
        {
            this.artwork.add(artwork.getBinaryData());
        }
    }

    void addGenre(String genre)
    {
        if (genre.equals(""))
        {
            this.genre.add("GENRE_MISSING");
        }
        else
        {
            this.genre.add(genre);
        }
    }

    void addLyrics(String lyrics)
    {
        if (lyrics.equals(""))
        {
            this.lyrics.add("LYRICS_MISSING");
        }
        else
        {
            this.lyrics.add(lyrics);
        }
    }

    /**
     * Returns ArrayList of String, each containing the full filepath of an audio file.
     */
    public ArrayList<String> getPath()
    {
        return path;
    }
    /**
     * Returns ArrayList of String, each containing the title of an audio file.
     */
    public ArrayList<String> getTitle()
    {
        return title;
    }
    /**
     * Returns ArrayList of String, each containing the artist of an audio file.
     */
    public ArrayList<String> getArtist()
    {
        return artist;
    }
    /**
     * Returns ArrayList of String, each containing the related album of an audio file.
     */
    public ArrayList<String> getAlbum()
    {
        return album;
    }
    /**
     * Returns ArrayList of a byte array, each containing the artwork of an audio file.
     */
    public ArrayList<byte[]> getArtwork()
    {
        return artwork;
    }
    /**
     * Returns ArrayList of String, each containing the genre of an audio file.
     */
    public ArrayList<String> getGenre()
    {
        return genre;
    }
    /**
     * Returns ArrayList of String, each containing the lyrics of an audio file.
     */
    public ArrayList<String> getLyrics()
    {
        return lyrics;
    }

    /**
     * This class handles parsing all audio files that exist recurisvely from a given root directory.
     * Heavily modified from original posting on stackoverflow.
     * Dheeraj Vepakomma, 2012, How to recursively scan directories in Android, July 14 [Online]. Available from: https://stackoverflow.com/questions/11482204/how-to-recursively-scan-directories-in-android
     */
    public static class FileHandler
    {
     private final List<File> absoluteFiles = new ArrayList<>();
     private final List<String> cleanList = new ArrayList<>();


        /**
         * Recursively calls itself, walking down from a root filepath, adding to a list all files, ignoring any directories.
         */
        public void walk(File root)
        {
            File[] list = root.listFiles();

            for (File file : list)
            {
                if (file.isDirectory())
                {
                    walk(file);// is directory, move to next.
                } else
                {
                    absoluteFiles.add(file); //is absolute file path, add to list.
                }
            }
        }

        /**
         * Removes non-audio files from the list obtained by walk().
         * Without calling this method rogue files will crash the app (example - trying to pull the metadata of a pdf, txt, etc.)
         * Returns the final, clean list of audio files.
         */
        public List<String> purge()
        {
            ArrayList<String> accepted = new ArrayList<>();
            accepted.add("mp3");
            accepted.add("flac");
            accepted.add("aac");
            accepted.add("ogg");
            accepted.add("wav");
            accepted.add("m4a");

            for (File file: absoluteFiles)
            {
              String extension = FilenameUtils.getExtension(file.getAbsolutePath());
              for (int i = 0; i < accepted.size(); i++)
              {
                  if (extension.equals(accepted.get(i)))
                  {
                      cleanList.add(file.getAbsolutePath());
                  }
              }
            }
            return cleanList;
        }
    }
}
