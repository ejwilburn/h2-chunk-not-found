/*
 * Copyright (c) 2024 Validity Inc.
 * All rights reserved.
 */

package com.validity.h2bug.database;

import com.validity.h2bug.exception.H2ChunkNotFoundBugException;
import org.h2.tools.Server;

import java.sql.SQLException;

public class H2ServerProvider {

    public static final String H2_WEB_CONSOLE_START_ERROR_MESSAGE = "Error starting H2 web console.";
    
    public static void startServer() {
        try {
            System.out.println("Starting H2 server.");

            Server.createWebServer("-webPort", "8080").start();

            System.out.println("H2 server started.");
        } catch (SQLException ex) {
            throw new H2ChunkNotFoundBugException(H2_WEB_CONSOLE_START_ERROR_MESSAGE, ex);
        }
    }
    
}
