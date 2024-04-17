/*
 * Copyright (c) 2024 Validity Inc.
 * All rights reserved.
 */

package com.validity.h2bug.database;

import com.validity.h2bug.domain.GeneratedKey;
import com.validity.h2bug.exception.H2ChunkNotFoundBugException;

import java.sql.Connection;
import java.sql.SQLException;

public class GeneratedKeyDaoService {

    private final Connection conn = DatabaseProvider.getInstance().getConnection();

    public void insert(GeneratedKey generatedKey) {
        var insertStatement = """
            INSERT INTO generated_key (id, task_id, record_id, scenario_id, object_type, full_key, hashed_key)
            values (?, ?, ?, ?, ?, ?, ?)
            """;

        try (var preparedStatement = conn.prepareStatement(insertStatement)) {
            preparedStatement.setString(1, generatedKey.getId().toString());
            preparedStatement.setString(2, generatedKey.getTaskId().toString());
            preparedStatement.setString(3, generatedKey.getRecordId());
            preparedStatement.setString(4, generatedKey.getScenarioId().toString());
            preparedStatement.setString(5, generatedKey.getObjectType());
            preparedStatement.setString(6, generatedKey.getFullKey());
            preparedStatement.setString(7, generatedKey.getHashedKey());
            preparedStatement.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            throw new H2ChunkNotFoundBugException("Error inserting generated key.", ex);
        }
    }

    public void truncateTable() {
        var truncateStatement = "TRUNCATE TABLE generated_key";
        try (var truncateJob = conn.prepareStatement(truncateStatement)) {
            truncateJob.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            throw new H2ChunkNotFoundBugException("Error truncating generated_key table.", ex);
        }
    }
    
}
