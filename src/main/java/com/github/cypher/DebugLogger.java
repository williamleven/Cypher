package com.github.cypher;

public class DebugLogger {
    public static final boolean ENABLED = true;

    public static void log(Object o){
        if (ENABLED) {
            System.out.println(o.toString());
        }
    }
}

/*
* Use
* if (DebugLogger.Enabled) {
*   DebugLogger.log("text");
* }
*
* Writing just DebugLogger.log(...); requires the program to format the message even when debugging is disabled.
*
* */
