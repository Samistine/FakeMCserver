package me.vemacs.fakemcserver.queries;

import java.util.List;
import me.vemacs.fakemcserver.ChatConverter;
import me.vemacs.fakemcserver.Message;
import me.vemacs.fakemcserver.data.Player;
import me.vemacs.fakemcserver.data.StatusResponse;
import com.samistine.samistinerest.SamistineRest;
import com.samistine.samistinerest.objects.InfoServers;
import com.samistine.samistinerest.objects.infoservers.RServer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import me.vemacs.fakemcserver.SLPResponder;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class SamistineQuery implements SLPResponder {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();
    private final SamistineRest rest;
    private final Cache cache;

    String restAPI;
    long restAPICache;
    String server_motd;
    String server_version;
    int server_onlineplayers;
    int server_maxplayers;
    boolean server_SendPlayerSample;
    int server_protocol;
    String server_favicon;

    public SamistineQuery() throws IOException {

        File file = new File("SamistineQuery.yml");
        if (!file.exists()) {
            Files.copy(ClassLoader.getSystemResourceAsStream("resources/SamistineQuery.yml"), file.toPath());
        }
        Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

        this.restAPI = config.getString("RestAPI", "http://samistine.com:5001/info/servers");
        this.restAPICache = config.getLong("CacheValidity", 300);
        this.server_motd = config.getString("MOTD", "Samistine Network");
        this.server_version = config.getString("Version", "SamistineRestQuerier");
        this.server_onlineplayers = config.getInt("OnlinePlayers", -1);
        this.server_maxplayers = config.getInt("MaxPlayers", -1);
        this.server_SendPlayerSample = config.getBoolean("SendPlayerSample", true);
        this.server_protocol = config.getInt("Protocol", -666);
        this.server_favicon = config.getString("Favicon", null);

        this.rest = new SamistineRest(restAPI);
        this.cache = new Cache(restAPICache);//in milliseconds
    }

    @Override
    public synchronized StatusResponse getStatusResponse(int protocol) {
        if (cache.isValid()) {
            LOGGER.log(Level.INFO, "Returning cached query");
            return (StatusResponse) cache.getCachedObject();
        }
        try {
            InfoServers iservers = rest.doInfoServers();
            if (iservers.success) {
                StatusResponse response = new StatusResponse(
                        server_version,
                        (server_protocol == -2 ? protocol : server_protocol),
                        (server_maxplayers == -1 ? iservers.extra.max_players : server_maxplayers),
                        (server_onlineplayers == -1 ? iservers.extra.players_online : server_onlineplayers),
                        (server_SendPlayerSample ? getAllPlayers(iservers.extra.servers) : null),
                        getAsMessage(server_motd),
                        server_favicon);
                cache.updateCache(response);
                return response;
            }
            LOGGER.log(Level.ERROR, "REST API returned false for 'success'");
            return null;
        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Unable to connect to Samistine REST API", ex);
            return null;
        }
    }

    public Player[] getAllPlayers(List<RServer> servers) {
        return servers.stream()
                .filter(server -> server != null)
                .map(server -> server.players)
                .flatMap(players -> players.stream())
                .map(playerToConvert -> new Player(playerToConvert.name, playerToConvert.uuid))
                .toArray(size -> new Player[size]);
    }

    public Message getAsMessage(String message) {
        return new Message(ChatConverter.replaceColors(message).replace((CharSequence) "\\n", (CharSequence) "\n"));
    }

    private static class Cache {

        final long cacheValidity;

        long validBefore = -1;
        Object cachedObject;

        public Cache(long cacheValidity) {
            this.cacheValidity = cacheValidity;
        }

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
