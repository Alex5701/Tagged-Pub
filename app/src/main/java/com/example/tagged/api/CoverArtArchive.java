package com.example.tagged.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

/**
 * This class implements requests to CoverArtArchive API and parsing the image data reply.
 */
public class CoverArtArchive
{
    private Bitmap artwork;
    /**
     * This method takes a MusicBrainz Identifier, which should be a release identifier.
     * Using this MBID, it makes a request to the CoverArtArchive API, and saving the reply to a bitmap via BitmapFactory.decodeStream().
     * Finally, it returns the Bitmap.
     */
    public void request(String mbid) throws IOException
    {
        String BASE_URL = "https://coverartarchive.org/release/";
        URL url = new URL(BASE_URL + mbid + "/front-250");
        HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
        https.setRequestMethod("GET");
        https.connect();
        artwork = BitmapFactory.decodeStream(https.getInputStream());
        https.disconnect();
    }

    /**
     * Return the bitmap that has been retrieved from the CoverArtArchive API.
     */
    public Bitmap getArtwork()
    {
        return this.artwork;
    }
}
