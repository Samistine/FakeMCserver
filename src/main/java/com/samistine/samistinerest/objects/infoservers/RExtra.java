package com.samistine.samistinerest.objects.infoservers;

import java.util.List;

/**
 * Created by Samuel on 6/9/2015.
 */
public class RExtra {
    public int max_players;
    /** Number of servers in the Bungeecord config **/
    public int number_of_servers;
    /** Total amount of players online **/
    public int players_online;
    /** Servers **/
    public List<RServer> servers;
}
