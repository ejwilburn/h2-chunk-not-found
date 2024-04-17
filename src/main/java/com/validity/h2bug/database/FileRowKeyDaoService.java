/*
 * Copyright (c) 2024 Validity Inc.
 * All rights reserved.
 */

package com.validity.h2bug.database;

import com.validity.h2bug.domain.FileRowKey;
import com.validity.h2bug.exception.H2ChunkNotFoundBugException;

import java.sql.Connection;
import java.sql.SQLException;

public class FileRowKeyDaoService {

    private final Connection conn = DatabaseProvider.getInstance().getConnection();

    public void insert(FileRowKey fileRowKey) {
        var insertStatement = """
            INSERT INTO file_row_key (id, task_id, file_row_id, scenario_id, hashed_key)
            values (?, ?, ?, ?, ?)
            """;

        try (var preparedStatement = conn.prepareStatement(insertStatement)) {
            preparedStatement.setString(1, fileRowKey.getId().toString());
            preparedStatement.setString(2, fileRowKey.getTaskId().toString());
            preparedStatement.setLong(3, fileRowKey.getFileRowId());
            preparedStatement.setString(4, fileRowKey.getScenarioId().toString());
            preparedStatement.setString(5, fileRowKey.getHashedKey());
            preparedStatement.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            throw new H2ChunkNotFoundBugException("Error inserting file row key.", ex);
        }
    }

    public void truncateTable() {
        var truncateStatement = "TRUNCATE TABLE file_row_key";
        try (var truncateJob = conn.prepareStatement(truncateStatement)) {
            truncateJob.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            throw new H2ChunkNotFoundBugException("Error truncating file_row_key table.", ex);
        }
    }
    
}
