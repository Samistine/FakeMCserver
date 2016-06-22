package com.samistine.samistinerest.objects;

import com.samistine.samistinerest.objects.infoservers.RExtra;

/**
 * Created by Samuel on 6/9/2015.
 *
 * Represents the data received from
 * <b>http://samistine.com:5001/info/servers</b>
 */
public class InfoServers {

    /** Returns "Servers found." usually **/
    public String Message;
    /** The most important data **/
    public RExtra extra;
    /** If the api was able to get the servers **/
    public boolean success;

}
