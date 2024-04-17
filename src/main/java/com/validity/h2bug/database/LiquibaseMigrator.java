/*
 * Copyright (c) 2024 Validity Inc.
 * All rights reserved.
 */

package com.validity.h2bug.database;

import com.validity.h2bug.exception.H2ChunkNotFoundBugException;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;

public class LiquibaseMigrator {

    public static final String LIQUIBASE_MIGRATION_ERROR_MESSAGE = "Error performing Liquibase migration.";
    
    // Hide the constructor as per SonarQube's suggestion.
    private LiquibaseMigrator() {}

    public static void runMigration(Connection connection) {
        try {
            System.out.println("Starting Liquibase.");
            
            Database liquibaseDb = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase lq = new Liquibase("config/liquibase/changelog.yml",
                new ClassLoaderResourceAccessor(), liquibaseDb);
            lq.update("dev");
            
            System.out.println("Liquibase done.");
        } catch (Exception ex) {
            throw new H2ChunkNotFoundBugException(LIQUIBASE_MIGRATION_ERROR_MESSAGE, ex);
        }
    }

}
