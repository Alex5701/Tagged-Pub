package com.example.tagged.api;

import android.util.Xml;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * This class implements requests to the ChartLyrics API and parsing the XML reply.
 */
public class ChartLyrics
{
    final String BASE_URL1 = "http://api.chartlyrics.com/apiv1.asmx/SearchLyric?";
    final String BASE_URL2 = "http://api.chartlyrics.com/apiv1.asmx/GetLyric?";
    private String artist;
    private String title;
    private String id = "missing";
    private String checksum = "missing";
    private String lyrics = "Lyrics not available from chartlyrics.com";

    /**
     * This is the main method of the class. This method takes an artist name and song title and returns the associated lyrics if they are available.
     * If lyrics are unavailable, this method will return a String explaining they are unavailable.
     */
    public void request(String artist, String title)
    {
        this.artist = artist;
        this.title = title;

        requestInitial(artist,title);
        if (!id.equals("missing") || !checksum.equals("missing"))
        {
            requestSecondary();
        }
    }

    /**
     *This method takes an artist name and song title, and uses that information to query the ChartLyrics API.
     * Finally, it calls parseInital() and gives it an inputstream.
     */
    private void requestInitial(String artist, String title)
    {
        try
        {
            URL url = new URL(BASE_URL1 + "artist=" + artist + "&song=" + title);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            try
            {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                parseInitial(in);
            } finally
            {
                urlConnection.disconnect();
            }
        } catch (IOException | XmlPullParserException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method takes an inputstream and assigns it to an xmlpullparser, which is then passed to readInital().
     */
    private void parseInitial(InputStream in) throws XmlPullParserException, IOException
    {
        try
        {
            /* Setup XmlPullParser */
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            /* Process XML */
            ArrayList<String> tagCache = new ArrayList<>();
            ArrayList<String> lyricId = new ArrayList<>();
            ArrayList<String> lyricChecksum = new ArrayList<>();
            ArrayList<String> artist = new ArrayList<>();
            ArrayList<String> title = new ArrayList<>();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                if (eventType == XmlPullParser.START_TAG)
                {
                    tagCache.add(parser.getName());

                    if (tagCache.get(tagCache.size() - 1).equals("Artist"))
                    {
                        artist.add(parser.nextText());
                    }
                    if (tagCache.get(tagCache.size() - 1).equals("Song"))
                    {
                        title.add(parser.nextText());
                    }
                    if (tagCache.get(tagCache.size() - 1).equals("LyricChecksum"))
                    {
                        lyricChecksum.add(parser.nextText());
                    }
                    if (tagCache.get(tagCache.size() - 1).equals("LyricId"))
                    {
                        String id = parser.nextText();
                        if (id.equals("0"))
                        {
                            /*LyricId is 0, this means there are no lyrics available. Remove this search result from the arraylists */
                            artist.remove(artist.size() - 1);
                            title.remove(title.size() - 1);
                        }
                        else
                        {
                            lyricId.add(id);
                        }
                    }
                }
                eventType = parser.next();
            }

            /*The API search function will return many results, so to find the correct checksum and ID apply the Levenshtein distance function to the title used to search and the title returned from the API.
            * 3 is given to account for special symbols or spaces added in error. More testing needs doing on this to find the perfect balance between accounting for error and returning the correct lyrics. */
            LevenshteinDistance ld = new LevenshteinDistance();
            for (int i = 0; i < lyricId.size(); i ++)
            {
                int result = ld.apply(this.title, title.get(i));
                if(result <= 3) {
                    this.id = lyricId.get(i);
                    this.checksum = lyricChecksum.get(i);
                }
            }
        } finally
        {
            in.close();
        }
    }



    /**
     * This method uses the global variables lyric checksum and lyric id and makes a second request to ChartLyrics.
     *  Finally, it calls parseSecondary() and gives it an inputstream.
     */
    private void requestSecondary()
    {
        try
        {
            URL url = new URL(BASE_URL2 + "lyricid=" + id + "&lyricCheckSum=" + checksum);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            try
            {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                parseSecondary(in);
            } finally
            {
                urlConnection.disconnect();
            }
        } catch (IOException | XmlPullParserException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method takes an inputstream and assigns it to an xmlpullparser, which is then passed to readSecondary().
     */
    private void parseSecondary(InputStream in) throws XmlPullParserException, IOException
    {
        try
        {
            /* Setup XmlPullParser */
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            /* Process XML */
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                if (eventType == XmlPullParser.START_TAG)
                {
                    if (parser.getName().equals("Lyric"))
                    {
                        lyrics = parser.nextText();
                    }
                }
                eventType = parser.next();
            }
        } finally
        {
            in.close();
        }
    }

    public String getLyrics()
    {
        return this.lyrics;
    }
}


