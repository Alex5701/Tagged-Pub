package com.example.tagged.song;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.tagged.R;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import java.io.File;
import java.io.IOException;

/**
 * Fragment for showing a single song item. Displays current song metadata and provides the user with the option of overwriting it.
 */
public class SongFragment extends Fragment implements View.OnClickListener
{
    SongViewModel songViewModel;
    private Song song;
    private EditText titleEdit;
    private EditText artistEdit;
    private EditText albumEdit;
    private EditText genreEdit;
    private EditText lyricsEdit;
    Button button;

    public SongFragment()
    {
        super(R.layout.song_fragment);
    }

    public static SongFragment newInstance()
    {
        return new SongFragment();
    }


    @Override
    public void onClick(View view)
    {
            Thread thread = new Thread(() ->
            {
                String filepath = song.getFilepath();
                String title = String.valueOf(titleEdit.getText());
                String artist = String.valueOf(artistEdit.getText());
                String album = String.valueOf(albumEdit.getText());
                String genre = String.valueOf(genreEdit.getText());
                String lyrics = String.valueOf(lyricsEdit.getText());
                if (!title.equals("")) //enable updating of just one piece of metadata, without erasing all others.
                {
                    songViewModel.updateTitle(filepath,title);
                }
                if (!artist.equals(""))
                {
                    songViewModel.updateArtist(filepath, artist);
                }
                if (!album.equals(""))
                {
                    songViewModel.updateAlbum(filepath, album);
                }
                if (!genre.equals(""))
                {
                    songViewModel.updateGenre(filepath, genre);
                }
                if (!lyrics.equals(""))
                {
                    songViewModel.updateLyrics(filepath,lyrics);
                }

                if (Environment.isExternalStorageManager())
                {
                    AudioFile f;
                    try
                    {
                        f = AudioFileIO.read(new File(filepath));
                        Tag tag = f.getTag();
                        if (!title.equals(""))
                        {
                            tag.setField(FieldKey.TITLE, title);
                        }
                        if (!artist.equals(""))
                        {
                            tag.setField(FieldKey.ARTIST, artist);
                        }
                        if (!album.equals(""))
                        {
                            tag.setField(FieldKey.ALBUM, album);
                        }
                        if (!genre.equals(""))
                        {
                            tag.setField(FieldKey.GENRE, genre);
                        }
                        if (!lyrics.equals(""))
                        {
                            tag.setField(FieldKey.LYRICS, lyrics);
                        }
                        AudioFileIO.write(f);
                    } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | CannotWriteException | InvalidAudioFrameException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
    }

    /**
     * Method calls SongViewModel to get a Song instance of the relevant track, holding it's metadata.
     */
    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        Thread thread = new Thread(() ->
        {
           songViewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);
           Bundle b = getArguments();
           if (b != null)
           {
               song = songViewModel.getSong(b.getString("filepath"));
           }
        });
       thread.start();
    }


    /**
     * Method handles getting references to Views in the fragment and setting the data to them that has been pulled from room database.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ImageView artworkView = (ImageView) view.findViewById(R.id.artworkview); //grab views
        TextView filepathView = (TextView) view.findViewById(R.id.filepathview);
        TextView titleView = (TextView) view.findViewById(R.id.titleview);
        titleEdit = (EditText) view.findViewById(R.id.titleedit);
        TextView artistView = (TextView) view.findViewById(R.id.artistview);
        artistEdit = (EditText) view.findViewById(R.id.artistedit);
        TextView albumView = (TextView) view.findViewById(R.id.albumview);
        albumEdit = (EditText) view.findViewById(R.id.albumedit);
        TextView genreView = (TextView) view.findViewById(R.id.genreview);
        genreEdit = (EditText) view.findViewById(R.id.genreedit);
        TextView lyricsView = (TextView) view.findViewById(R.id.lyricsview);
        lyricsEdit = (EditText) view.findViewById(R.id.lyricsedit);
        button = (Button) view.findViewById(R.id.savebutton);
        button.setOnClickListener(this);
        artworkView.setImageBitmap(BitmapFactory.decodeByteArray(song.getArtwork(), 0, song.getArtwork().length)); //write data to views
        filepathView.setText("filepath:\n" + song.getFilepath()+ "\n");
        titleView.setText("Title:\n" + song.getTitle() + "\n");
        artistView.setText("Artist:\n" + song.getArtist()+ "\n");
        albumView.setText("Album:\n" + song.getAlbum()+ "\n");
        genreView.setText("Genre:\n" + song.getGenre()+ "\n");
        lyricsView.setText("Lyrics:\n" + song.getLyrics() + "\n");
    }
}
