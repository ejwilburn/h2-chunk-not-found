/*
 * Copyright (c) 2024 Validity Inc.
 * All rights reserved.
 */

package com.validity.h2bug;

import com.validity.h2bug.database.*;
import com.validity.h2bug.domain.FileRowKey;
import com.validity.h2bug.domain.GeneratedKey;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

public class H2ChunkNotFoundBug {

    public static final String H2_DB_DIR = "D:\\";
    protected static final String H2_DB_MAIN_EXTENSION = ".mv.db";
    protected static final String H2_DB_TRACE_EXTENSION = ".trace.db";

    private static final int NUMBER_OF_DUPLICATE_GROUPS = 400000;
    private static final int GROUP_SIZE = 10;
    private static final int TOTAL_MATCHES = NUMBER_OF_DUPLICATE_GROUPS * GROUP_SIZE;

    private final FileRowKeyDaoService fileRowKeyDaoService = new FileRowKeyDaoService();
    private final GeneratedKeyDaoService generatedKeyDaoService = new GeneratedKeyDaoService();
    private final ImportDataDaoService importDataDaoService = new ImportDataDaoService();

    public static void main(String[] args) {
        var stopWatch = new StopWatch();
        stopWatch.start();
        var h2Bug = new H2ChunkNotFoundBug();

        try {
            h2Bug.init();
            h2Bug.runTest();
        } finally {
            stopWatch.stop();
            System.out.println("Total duration: %s".formatted(stopWatch.formatTime()));
            h2Bug.stop();
        }
    }

    public void init() {
        H2ServerProvider.startServer();

        // Delete the DB when the app exits, so we start with a fresh DB every time.
        // Comment this section out if you want to keep the database after running.
        var dbFileName = Path.of(H2_DB_DIR, DatabaseProvider.DB_NAME).toString();
        new File(dbFileName + H2_DB_MAIN_EXTENSION).deleteOnExit();
        new File(dbFileName + H2_DB_TRACE_EXTENSION).deleteOnExit();

        var dbConnection = DatabaseProvider.getInstance().getConnection();
        LiquibaseMigrator.runMigration(dbConnection);

        System.out.println("DB initialized.");
    }

    public void runTest() {
        var taskId = UUID.randomUUID();
        var scenarioId = UUID.randomUUID();

        System.out.println("Creating %d file rows with %d total matches.".formatted(NUMBER_OF_DUPLICATE_GROUPS, TOTAL_MATCHES));
        for (int fileRowIndex = 0; fileRowIndex < NUMBER_OF_DUPLICATE_GROUPS; fileRowIndex++) {
            var matchKey = buildHash(String.valueOf(fileRowIndex));

            var fileRowKey = FileRowKey.builder()
                .id(UUID.randomUUID())
                .taskId(taskId)
                .fileRowId((long)fileRowIndex)
                .scenarioId(scenarioId)
                .hashedKey(matchKey)
                .build();
            fileRowKeyDaoService.insert(fileRowKey);

            for (int matchIndex = 0; matchIndex < GROUP_SIZE; matchIndex++) {
                var crmRecordId = "%d.%d".formatted(fileRowIndex, matchIndex);

                var generetedKey = GeneratedKey.builder()
                    .id(UUID.randomUUID())
                    .taskId(taskId)
                    .scenarioId(scenarioId)
                    .objectType("Contact")
                    .recordId(crmRecordId)
                    .fullKey(String.valueOf(fileRowIndex))
                    .hashedKey(matchKey)
                    .build();
                generatedKeyDaoService.insert(generetedKey);
            }

            if ((fileRowIndex + 1) % 1000 == 0)
                System.out.println("Created %d file rows and %d matches..."
                    .formatted(fileRowIndex + 1, (fileRowIndex + 1) * GROUP_SIZE));
        }
        System.out.println("Done creating %d file rows with %d total matches."
            .formatted(NUMBER_OF_DUPLICATE_GROUPS, TOTAL_MATCHES));

        System.out.println("Adding matches to import data table...");
        importDataDaoService.addMatches(taskId, scenarioId);
        System.out.println("Done.");
    }

    private String buildHash(String value) {
        if (value != null) {
            return DigestUtils.sha256Hex(value);
        }

        return null;
    }

    public void stop() {
        System.out.println("Shutting down.");
        System.exit(0);
    }

}
