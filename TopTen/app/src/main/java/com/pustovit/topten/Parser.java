package com.pustovit.topten;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Pustovit Vladimir on 04.06.2019.
 * vovapust1989@gmail.com
 */

public class Parser {
    private static final String TAG = "Parser";
    private ArrayList<FeedEntry> applications;

    public Parser() {
        this.applications = new ArrayList<FeedEntry>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    public boolean parse(String xmlData) {
        boolean status = true;
        FeedEntry currentRecord = null;
        boolean inEntry = false;
        String textValue = "";

        boolean height75 = false;
        boolean height60 =false;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(xmlData));

            int eventType = xpp.getEventType();

            String tagName = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {

                tagName = xpp.getName();

                switch (eventType) {

                    case XmlPullParser.START_TAG:
//                        Log.d(TAG, "parse: Starting tag for " + tagName);
                        if ("entry".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            currentRecord = new FeedEntry();
                        }
                        if("image".equalsIgnoreCase(tagName) && inEntry){
                            String imageResolution = xpp.getAttributeValue(null,"height");
                            if(imageResolution != null){
                                height75 ="75".equalsIgnoreCase(imageResolution);
                                height60 = "60".equalsIgnoreCase(imageResolution);
                            }
                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
//                        Log.d(TAG, "parse: Ending tag for " + tagName);
                        if (inEntry) {
                            if ("entry".equalsIgnoreCase(tagName)) {
                                applications.add(currentRecord);
                                inEntry = false;
                            } else if ("name".equalsIgnoreCase(tagName)) {
                                currentRecord.setName(textValue);
                            } else if ("artist".equalsIgnoreCase(tagName)) {
                                currentRecord.setArtist(textValue);
                            } else if ("releasedate".equalsIgnoreCase(tagName)) {
                                currentRecord.setReleaseDate(textValue);
                            } else if ("summary".equalsIgnoreCase(tagName)) {
                                currentRecord.setSummary(textValue);
                            } else if ("image".equalsIgnoreCase(tagName)) {
                                if(height75 || height60){
                                    currentRecord.setImgUrl(textValue);
                                }
                            }
                        }
                        break;
                        default:
                            //nothing to do
                }
                eventType = xpp.next();

            }

//            for(FeedEntry app : applications){
//                Log.d(TAG, "******************************");
//                Log.d(TAG, app.toString());
//            }

        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }
}
