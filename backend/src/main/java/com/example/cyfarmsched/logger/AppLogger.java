package com.example.cyfarmsched.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AppLogger {
    private AppLogger() {
    }

    public static Logger getLogger(Class<?> type) {
        return LoggerFactory.getLogger(type);
    }
}
