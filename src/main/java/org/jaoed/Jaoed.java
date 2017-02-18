package org.jaoed;

import main.antlr4.org.jaoed.*;
import org.antlr.v4.runtime.*;

import org.jaoed.config.Config;
import org.jaoed.config.ConfigBuilder;

public class Jaoed {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("Need filename as first argument");
        }

        ConfigBuilder builder = new ConfigBuilder();
        builder.parseFile(args[0]);
        Config config = builder.getConfig();
        System.out.println(config);
    }
}
