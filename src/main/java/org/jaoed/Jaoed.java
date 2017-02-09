package org.jaoed;

import main.antlr4.org.jaoed.*;
import org.antlr.v4.runtime.*;

public class Jaoed {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("Need filename as first argument");
        }

        ConfigLexer lexer = new ConfigLexer(
            (CharStream) new ANTLRFileStream(args[0]));
        TokenStream tokenStream = new CommonTokenStream(lexer);
        ConfigParser parser = new ConfigParser(tokenStream);

        parser.dumpDFA();
    }
}
