package it.lorenzoval.deliverable1;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class Deliverable1 {

    private static final String PROJECT = "VCL";
    private static final String API_URL = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22{0}%22" +
            "AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR%22status%22=%22resolved%22)AND" +
            "%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt={1}&maxResults={2}";
    private static final Logger logger = Logger.getLogger(Deliverable1.class.getName());

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream in = new URL(url).openStream()) {
            return new JSONObject(IOUtils.toString(in, StandardCharsets.UTF_8));
        }
    }

    public static void main(String[] args) {
        int i = 0;
        int j;
        int total = 1;
        String url;

        do {
            j = i + 1000;
            url = java.text.MessageFormat.format(API_URL, PROJECT, Integer.toString(i), Integer.toString(j));

            try {
                JSONObject json = readJsonFromUrl(url);
                JSONArray issues = json.getJSONArray("issues");
                total = json.getInt("total");

                for (; i < total && i < j; i++) {
                    //Iterate through each bug
                    String key = issues.getJSONObject(i % 1000).get("key").toString();
                    logger.log(Level.INFO, key);
                }

            } catch (IOException ioe) {
                logger.log(Level.WARNING, ioe.toString());
            }

        } while (i < total);

    }

}
