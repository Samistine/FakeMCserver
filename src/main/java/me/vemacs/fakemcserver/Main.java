package me.vemacs.fakemcserver;

import me.vemacs.fakemcserver.queries.SamistineQuery;

public class Main {

    public static String version;
    public static int protocol;

    public static void main(String[] args) throws Exception {

        int port = 25565;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new SLPServer(port, new SamistineQuery()).run();
    }
}
