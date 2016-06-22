package me.vemacs.fakemcserver.data;

import me.vemacs.fakemcserver.Message;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class StatusResponse {

    static final Logger LOGGER = LogManager.getLogger();

    final Version version;
    final Players players;
    final Message description;
    final String favicon;

    public StatusResponse(String name, int protocol, int max, int online, Player[] sample, Message description, String favicon) {
        this.version = new Version(name, protocol);
        this.players = new Players(max, online, sample);
        this.description = description;
        this.favicon = favicon;
    }

    static class Version {

        final String name;
        final int protocol;

        Version(String name, int protocol) {
            this.name = name;
            this.protocol = protocol;
        }
    }

    static class Players {

        final int max;
        final int online;
        final Player[] sample;

        Players(int max, int online, Player[] sample) {
            this.max = max;
            this.online = online;
            this.sample = sample;
        }
    }
}
