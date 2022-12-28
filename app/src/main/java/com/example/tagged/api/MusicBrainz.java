package com.example.tagged.api;

import android.util.Xml;

import com.example.tagged.meta.Meta;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements requests to the MusicBrainz API and parsing the XML reply.
 */
public class MusicBrainz
{
        final String BASE_URL = "https://musicbrainz.org/ws/2/";
        ArrayList<Meta> metaData = new ArrayList<>();


    /**
     * This method takes a MusicBrainz identifier, which should be a track identifier.
     * Using this MBID, it makes a request to the MusicBrainz API.
     * Finally, it calls parse() and gives it an inputstream.
     */
    public void request(String mbid) throws XmlPullParserException
        {
            try
            {
                URL url = new URL(BASE_URL + "recording/" + mbid + "?inc=artists+releases+genres");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                try
                {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    parseReply(in);
                } finally
                {
                    urlConnection.disconnect();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    /**
     * This method takes an inputstream and assigns it to an xmlpullparser, which is then passed to readFeed().
     */
        private void parseReply(InputStream in) throws XmlPullParserException, IOException
        {
            try
            {
                /* Setup parser */
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                parser.nextTag();
                int eventType = parser.getEventType();

                /* Process XML */
                ArrayList<String> tagCache = new ArrayList<>();
                String title = "missing";
                String artist = "missing";
                String genre = "missing";
                List<String> albums = new ArrayList<>();
                List<String> releaseIDs = new ArrayList<>();

                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    if (eventType == XmlPullParser.START_TAG || eventType == XmlPullParser.END_TAG)
                    {
                        tagCache.add(parser.getName());

                        if (eventType == XmlPullParser.START_TAG && tagCache.size() > 2)
                        {
                            if(tagCache.get(tagCache.size() - 1).equals("title") && tagCache.get(tagCache.size() - 3).equals("metadata")) //track title
                            {
                                title = parser.nextText();
                            }
                            if(tagCache.get(tagCache.size() - 1).equals("name") && tagCache.get(tagCache.size() - 2).equals("artist")) //artist name
                            {
                                artist = parser.nextText();
                            }

                            if(tagCache.get(tagCache.size() - 1).equals("name") && tagCache.get(tagCache.size() - 2).equals("genre"))
                            {
                                genre = parser.nextText();
                            }

                            if(tagCache.get(tagCache.size() - 1).equals("release") && tagCache.get(tagCache.size() - 2).equals("release-list"))
                            {
                                releaseIDs.add(parser.getAttributeValue(0));
                            }

                            if(tagCache.get(tagCache.size() - 1).equals("release") &&tagCache.get(tagCache.size() - 2).equals("release"))
                            {
                                releaseIDs.add(parser.getAttributeValue(0));
                            }

                            if(tagCache.get(tagCache.size() - 1).equals("title") && tagCache.get(tagCache.size() - 2).equals("release"))
                            {
                                albums.add(parser.nextText());
                            }

                        }
                    }
                    eventType = parser.next();
                }
                for (int i = 0; i < releaseIDs.size(); i++)
                {
                    metaData.add(new Meta(title,artist,albums.get(i),genre,releaseIDs.get(i)));
                }
            } finally
            {
                in.close();
            }
        }


    /**Returns the Meta ArrayList containing all release metadata related to the input MBID.*/
    public ArrayList<Meta> getMetaData()
        {
            return this.metaData;
        }
}
