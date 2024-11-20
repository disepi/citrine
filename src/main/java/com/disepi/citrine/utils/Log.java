package com.disepi.citrine.utils;

import cn.nukkit.utils.Logger;

public class Log {
    public static Logger log;

    // Set the logger instance
    public static void setLogger(Logger logger) {
        Log.log = logger;
    }

    // Log a string to the console
    public static void s(String string) {
        log.info(string);
    }

}
