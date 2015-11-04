/*
 * Decompiled with CFR 0_102.
 */
package me.vemacs.fakemcserver.queries;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.vemacs.fakemcserver.ChatConverter;
import me.vemacs.fakemcserver.Main;
import me.vemacs.fakemcserver.Message;
import me.vemacs.fakemcserver.data.Player;
import me.vemacs.fakemcserver.data.StatusResponse;
import me.vemacs.fakemcserver.queries.samistinerest.SamistineRest;
import me.vemacs.fakemcserver.queries.samistinerest.objects.InfoServers;
import me.vemacs.fakemcserver.queries.samistinerest.objects.infoservers.RPlayer;
import me.vemacs.fakemcserver.queries.samistinerest.objects.infoservers.RServer;

public class SamistineQuery implements Response {

    private static final long cacheValidity = 300;
    private static final Cache cache = new Cache();
    private final SamistineRest rest = new SamistineRest();

    @Override
    public synchronized StatusResponse getStatusResponse() {
        if (cache.isValid()) {
            System.out.println("Returning cached query");
            return (StatusResponse) cache.getCachedObject();
        }
        try {
            InfoServers iservers = rest.doInfoServers();
            if (iservers.success) {
                StatusResponse response = new StatusResponse(
                        Main.version,
                        Main.protocol,
                        iservers.extra.max_players,
                        iservers.extra.players_online,
                        getPlayersForResponse(getAllPlayers(iservers.extra.servers)),
                        getAsMessage("Samistine Network"),
                        null);
                cache.updateCache(response);
                return response;
            }
            System.out.println("REST API returned false for 'success'");
            return null;
        } catch (Exception ex) {
            Logger.getLogger(SamistineQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Unable to connect to Samistine REST API");
            return null;
        }
    }

    public List<RPlayer> getAllPlayers(List<RServer> servers) {
        ArrayList<RPlayer> players = new ArrayList<>();
        for (RServer server : servers) {
            if (server != null) {
                for (RPlayer player : server.players) {
                    if (player != null) {
                        players.add(player);
                    }
                }
            }
        }
        return players;
    }

    public Player[] getPlayersForResponse(List<RPlayer> playersToConvert) {
        ArrayList<Player> players = new ArrayList<>(playersToConvert.size());
        for (RPlayer playerToConvert : playersToConvert) {
            Player player = new Player(playerToConvert.name, playerToConvert.uuid);
            players.add(player);
        }
        return players.toArray(new Player[players.size()]);
    }

    public Message getAsMessage(String message) {
        Message classicMsg = new Message();
        classicMsg.text = ChatConverter.replaceColors(message).replace((CharSequence) "\\n", (CharSequence) "\n");
        return classicMsg;
    }

    private static class Cache {

        long validBefore = -1;
        Object cachedObject;

        public Object getCachedObject() {
            return this.cachedObject;
        }

        public void updateCache(Object cachedObject) {
            this.cachedObject = cachedObject;
            this.validBefore = System.currentTimeMillis();
        }

        boolean isValid() {
            return this.validBefore >= System.currentTimeMillis() - cacheValidity;
        }
    }

}
