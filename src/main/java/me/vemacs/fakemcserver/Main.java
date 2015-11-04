package me.vemacs.fakemcserver;

import me.vemacs.fakemcserver.data.StatusResponse;

import java.io.*;
import java.util.Properties;
import me.vemacs.fakemcserver.queries.SamistineQuery;

public class Main {

    static Properties prop = new Properties();
    public static String version;
    public static int protocol;

    public static void main(String[] args) throws Exception {
        File config = new File("server.properties");

        if (!config.exists()) {
            try (OutputStream output = new FileOutputStream(config)) {
                prop.setProperty("version", "1.7.9");
                prop.setProperty("protocol", Integer.toString(5));
                //prop.setProperty("max", Integer.toString(42069));
                //prop.setProperty("online", Integer.toString(9001));
                //prop.setProperty("description", "&cBl&baze it\\n&fmaggots");
                //prop.setProperty("engine", "classic");
                //prop.store(output, null);
            }
        }

        try (InputStream input = new FileInputStream(config)) {
            prop.load(input);
            version = prop.getProperty("version");
            protocol = Integer.parseInt(prop.getProperty("protocol"));
        }

        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 25565;
        }
        new SLPServer(port).run();
    }

    public static StatusResponse getStatusResponse() {
        SamistineQuery responseImplementation = new SamistineQuery();
        return responseImplementation.getStatusResponse();
    }
}
