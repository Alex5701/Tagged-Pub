package com.example.tagged.meta;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tagged.R;
import com.example.tagged.api.AcoustID;
import com.example.tagged.api.ChartLyrics;
import com.example.tagged.api.CoverArtArchive;
import com.example.tagged.api.MusicBrainz;
import com.example.tagged.song.Song;
import com.example.tagged.song.SongViewModel;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.InvalidBoxHeaderException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class MetaFragment extends Fragment implements View.OnClickListener
{
    /**
     * Operational variables
     */
    SongViewModel svm;
    private Song song;
    Button requestButton;
    TextView filepathView;
    TextView titleView;
    TextView artistView;
    TextView recyclerHeader;
    RecyclerView recyclerMeta;
    MetaAdapter mAdapter;
    List<Meta> meta;

    /**
     * Api handler class instances
     */
    private final AcoustID aid = new AcoustID();
    private final MusicBrainz mb = new MusicBrainz();
    private final ChartLyrics cl = new ChartLyrics();
    private final CoverArtArchive caa = new CoverArtArchive();

    MetaFragment()
    {
        super(R.layout.meta_fragment);
    }

    public static MetaFragment newInstance()
    {
        return new MetaFragment();
    }


    /**
     * Overwrite onClick to pull metadata from APIs and set it to the recyclerview.
     */
    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.request_button)
        {
            recyclerHeader.setVisibility(View.VISIBLE);

            Thread thread = new Thread(() ->
            {
                try
                {
                    aid.request(song.getFilepath()); //call AcoustID, get MBID
                    mb.request(aid.getMBID()); //Use MBID to get Metadata
                    meta = mb.getMetaData();
                    for (int i = 0; i < meta.size(); i++)
                    {
                        caa.request(meta.get(i).getReleaseID());
                        meta.get(i).setArtwork(caa.getArtwork());
                    }
                } catch (XmlPullParserException | IOException e)
                {
                    e.printStackTrace();
                }
            });

            thread.start();
            try
            {
                /**Waiting for the thread to join before assigning the metadata to the recycler view.
                 * Without doing this, the app crashes. I assume this is because the meta object is initialised in the thread.*/
                thread.join();
                mAdapter.setMetadata(meta);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method saves the metadata set that the user has selected.
     * Overwrites the room database and attempts to overwrite the original file.
     * Even if it fails to overwrite the original file (which only seems to happen if permissions aren't given) it should still succeed in writing to the Room database.
     */
    public void saveMetadata(int id)
    {
        Meta metaSelected = meta.get(id); //get the metadata set the user chose to save
        Thread thread = new Thread(() ->
        {
            cl.request(metaSelected.getArtist(), metaSelected.getTitle());
            Bitmap artwork = metaSelected.getArtwork();
            if (artwork == null)
            {
                artwork = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.artworkmissing);
            }
            //updating room database..
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            artwork.compress(Bitmap.CompressFormat.PNG, 100, s);
            byte[] artworkByte = s.toByteArray();
            svm.updateTitle(song.getFilepath(), metaSelected.getTitle());
            svm.updateArtist(song.getFilepath(), metaSelected.getArtist());
            svm.updateAlbum(song.getFilepath(), metaSelected.getAlbum());
            svm.updateGenre(song.getFilepath(), metaSelected.getGenre());
            svm.updateArtwork(song.getFilepath(), artworkByte);
            svm.updateLyrics(song.getFilepath(), cl.getLyrics());

            //updating original file..
            File tempFile; //make tempfile
            try
            {
                tempFile = File.createTempFile("tempart", "tempart", getActivity().getCacheDir());
                OutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile)); //create outputstream
                artwork.compress(Bitmap.CompressFormat.PNG, 100, os); //write to file
                Artwork art = ArtworkFactory.createArtworkFromFile(tempFile); //create Jaudiotagger Artwork from file
                os.close();
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    AudioFile f = AudioFileIO.read(new File(song.getFilepath()));
                    Tag tag = f.getTag();
                    tag.addField(art);
                    tag.setField(FieldKey.TITLE, metaSelected.getTitle());
                    tag.setField(FieldKey.ARTIST, metaSelected.getArtist());
                    tag.setField(FieldKey.GENRE, metaSelected.getGenre());
                    tag.setField(FieldKey.ALBUM, metaSelected.getAlbum());
                    tag.setField(FieldKey.LYRICS, cl.getLyrics());
                    AudioFileIO.write(f);
                }
            } catch (IOException | CannotReadException | TagException | ReadOnlyFileException | InvalidAudioFrameException | CannotWriteException | InvalidBoxHeaderException e)
            {
                e.printStackTrace();
            }
        });
        try
        {
            thread.start();
            thread.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
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
            svm = new ViewModelProvider(requireActivity()).get(SongViewModel.class); //get viewmodel
            Bundle b = getArguments();
            song = svm.getSong(b.getString("filepath"));
        });
        thread.start();
        try
        {
            thread.join(); //avoid crashes by waiting for song data to be pulled before trying to display it.
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Method handles getting references to Views in the fragment and putting them in their default state.
     * Defines adapterInterface and passes it to the MetaAdapter.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        filepathView = view.findViewById(R.id.filepathview);
        filepathView.setText(song.getFilepath());
        titleView = view.findViewById(R.id.titleview);
        titleView.setText(song.getTitle());
        artistView = view.findViewById(R.id.artistview);
        artistView.setText(song.getArtist());
        requestButton = view.findViewById(R.id.request_button);
        requestButton.setOnClickListener(this);
        recyclerHeader = view.findViewById(R.id.recycler_header);
        recyclerHeader.setVisibility(View.INVISIBLE);
        recyclerMeta = view.findViewById(R.id.recycler_meta);


        //adapterInterface implementation, to be passed to MetaAdapter
        MetaAdapter.MetaAdapterInterface adapterInterface = this::saveMetadata;

        mAdapter = new MetaAdapter(getContext(), adapterInterface);
        recyclerMeta.setAdapter(mAdapter);
        recyclerMeta.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
