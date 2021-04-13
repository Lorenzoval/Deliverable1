package it.lorenzoval.deliverable1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.json.JSONObject;
import org.json.JSONArray;

public class Deliverable1 {

    private static final String PROJECT = "VCL";
    private static final String API_URL = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22{0}%22" +
            "AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR%22status%22=%22resolved%22)AND" +
            "%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt={1}&maxResults={2}";
    private static final Logger logger = Logger.getLogger(Deliverable1.class.getName());

    public static void writeToCSV(SortedMap<YearMonth, Integer> map, String filename) throws IOException {
        File file = new File(filename);
        List<String> list = new ArrayList<>();
        YearMonth begin = map.firstKey();
        YearMonth end = map.lastKey();
        for (; !begin.isAfter(end); begin = begin.plusMonths(1)) {
            Integer value = map.get(begin);
            if (null == value) {
                value = 0;
            }
            list.add(begin + "," + value);
        }
        FileUtils.writeLines(file, list);
    }

    public static void main(String[] args) {
        int i = 0;
        int j;
        int total = 1;
        String url;
        TreeMap<YearMonth, Integer> treeMap = new TreeMap<>();
        DateTimeFormatter fromAPIFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        do {
            j = i + 1000;
            url = MessageFormat.format(API_URL, PROJECT, Integer.toString(i), Integer.toString(j));

            try (InputStream in = new URL(url).openStream()) {
                JSONObject json = new JSONObject(IOUtils.toString(in, StandardCharsets.UTF_8));
                JSONArray issues = json.getJSONArray("issues");
                total = json.getInt("total");

                for (; i < total && i < j; i++) {
                    OffsetDateTime odt = OffsetDateTime.parse(issues.getJSONObject(i % 1000)
                            .getJSONObject("fields").get("resolutiondate").toString(), fromAPIFormatter);
                    treeMap.merge(YearMonth.from(odt), 1, (oldValue, unused) -> ++oldValue);
                }

            } catch (IOException ioe) {
                logger.log(Level.WARNING, ioe.toString());
            }

        } while (i < total);

        try {
            writeToCSV(treeMap, "out.csv");
        } catch (IOException ioe) {
            logger.log(Level.WARNING, ioe.toString());
        }

    }

}
