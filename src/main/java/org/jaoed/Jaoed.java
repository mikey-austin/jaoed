package org.jaoed;

import org.jaoed.service.App;
import org.jaoed.service.ConfigAppBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jaoed {
    private static final Logger LOG = LoggerFactory.getLogger(Jaoed.class);

    public static void main(String[] args) {
        try {
            App app = new ConfigAppBuilder(args[0]).build();
            Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
            app.start();
        } catch (Exception e) {
            LOG.error("an error occured whilst starting the application", e);
        }
    }
}
