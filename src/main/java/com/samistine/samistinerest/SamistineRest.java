package com.samistine.samistinerest;

import com.google.gson.Gson;
import com.samistine.samistinerest.objects.InfoServers;

/**
 * Created by Samuel on 6/9/2015.
 */
public class SamistineRest {

    /**
     * The web address of the Bungeecord Rest service *
     */
    private final String bungeeAPI;

    private final Gson gson = new Gson();
    private final HTTPClient webRequest = new HTTPClient();

    public SamistineRest(String bungeeAPI) {
        this.bungeeAPI = bungeeAPI;
    }

    /**
     *
     * Java api for accessing <b>{@link #bungeeAPI}</b>
     *
     * @return the JAVA representation of the rest service
     * @see com.samistine.samistinerest.objects.InfoServers
     */
    public InfoServers doInfoServers() throws Exception {
        String response = webRequest.get(bungeeAPI);
        return gson.fromJson(response, InfoServers.class);
    }
}
