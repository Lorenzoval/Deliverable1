package it.lorenzoval.deliverable1;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

public class Deliverable1 {

    private static final String PROJECT = "VCL";
    private static final String API_URL = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22" +
            PROJECT + "%22";
    private static final Logger logger = Logger.getLogger(Deliverable1.class.getName());

    public static void main(String[] args) {
        try (InputStream in = new URL(API_URL).openStream()) {
            String str = IOUtils.toString(in, StandardCharsets.UTF_8);
            logger.log(Level.INFO, str);
        } catch (IOException ioe) {
            logger.log(Level.WARNING, ioe.toString());
        }
    }

}
