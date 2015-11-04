package me.vemacs.fakemcserver.queries.samistinerest;

import com.google.gson.Gson;
import me.vemacs.fakemcserver.queries.samistinerest.objects.InfoServers;
import me.vemacs.fakemcserver.utils.HTTPClient;

/**
 * Created by Samuel on 6/9/2015.
 */
public class SamistineRest {

    /** The web address of the Bungeecord Rest service **/
    private final static String bungeeAPI = "http://samistine.com:5001/info/servers";

    private final Gson gson = new Gson();
    private final HTTPClient webRequest = new HTTPClient();


    /**
     *
     * Java api for accessing <b>{@link #bungeeAPI}</b>
     *
     * @return the JAVA representation of the rest service
     * @see me.vemacs.fakemcserver.queries.samistinerest.objects.InfoServers
     */
    public InfoServers doInfoServers() throws Exception {
        String response = webRequest.get(bungeeAPI);
        return gson.fromJson(response, InfoServers.class);
    }
}
