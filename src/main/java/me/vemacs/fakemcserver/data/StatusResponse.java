package me.vemacs.fakemcserver.data;

import me.vemacs.fakemcserver.Message;

public class StatusResponse {
    Version version;
    Players players;
    Message description;
    public String favicon;

    public StatusResponse(String name, int protocol, int max, int online, Player[] sample, Message description, String favicon) {
        this.version = new Version(name, protocol);
        this.players = new Players(max, online, sample);
        this.description = description;
        this.favicon = favicon;
    }

    public class Version {
        String name = "1.7.9";
        int protocol = 5;

        public Version(String name, int protocol) {
            this.name = name;
            this.protocol = protocol;
        }
    }

    class Players {
        int max = 9000;
        int online = 420;
        Player[] sample;

        Players(int max, int online, Player[] sample) {
            this.max = max;
            this.online = online;
            this.sample = sample;
        }
    }
}
