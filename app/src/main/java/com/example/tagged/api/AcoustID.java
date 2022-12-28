package com.example.tagged.api;

import android.util.Xml;

import com.geecko.fpcalc.FpCalc;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * This class implements requests to AcoustID API and parsing the XML reply.
 */
public class AcoustID
{
        final String BASE_URL = "https://api.acoustid.org/v2/lookup?client=";
        final String API_KEY = "";
        private String MBID = "missing";

    /**
     * This method takes a filepath and uses the fp-calc android library to generate a chromaprint, which is then used to query the AcoustID API.
     * Finally, it calls parse() and gives it an inputstream.
     */
    public void request(String filepath) throws XmlPullParserException
        {
            String result = FpCalc.fpCalc(new String[]{filepath});

            //cleanup result
            String[] s = result.split("\n");
            String duration = s[0].split("=")[1];
            String fingerprint = s[1].split("=")[1];

            try
            {
                URL url = new URL(BASE_URL + API_KEY + "&format=xml" + "&meta=recordingids" + "&duration=" + duration + "&fingerprint=" + fingerprint);
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
     * This method handles the implementation for parsing the xml reply data.
     * It pulls a MBID from the XML reply and assigns it to a global variable.
     */
        private void parseReply(InputStream in) throws XmlPullParserException, IOException
        {
            try
            {
                /* Setup XmlPullParser */
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                parser.nextTag();
                int eventType = parser.getEventType();

                /* Process XML */
                ArrayList<String> tagCache = new ArrayList<>();

                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    if (eventType == XmlPullParser.START_TAG || eventType == XmlPullParser.END_TAG)
                    {
                        tagCache.add(parser.getName());
                    }
                    if (tagCache.get(tagCache.size() - 1).equals("id") && tagCache.get(tagCache.size() - 2).equals("recording"))
                    {
                        MBID = parser.nextText();
                    }
                    eventType = parser.next();
                }
            } finally
            {
                in.close();
            }
        }

    /**
     * Returns a String containing either a MBID or "MISSING".
     */
    public String getMBID()
    {
        return MBID;
    }
}
