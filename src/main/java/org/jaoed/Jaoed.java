package org.jaoed;

import main.antlr4.org.jaoed.*;
import org.antlr.v4.runtime.*;

import org.jaoed.config.Config;

public class Jaoed {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("Need filename as first argument");
        }

        Config config = Config.parseFile(args[0]);
        System.out.println(config);
    }
}
