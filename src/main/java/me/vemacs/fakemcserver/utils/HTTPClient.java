package me.vemacs.fakemcserver.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Samuel on 6/9/2015.
 */
public class HTTPClient {
    private static final String USER_AGENT = "Mozilla/5.0";

    // HTTP GET request
    public String get(String url) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        // add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        //StringBuffer response = new StringBuffer();
        StringBuilder response = new StringBuilder();

        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        } finally {
            in.close();
        }


        // print result
        //System.out.println(response.toString());
        return response.toString();

    }

}
