/*
 * Copyright (c) 2024 Validity Inc.
 * All rights reserved.
 */

package com.validity.h2bug.database;

import com.validity.h2bug.exception.H2ChunkNotFoundBugException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class ImportDataDaoService {

    private final Connection conn = DatabaseProvider.getInstance().getConnection();

    public void addMatches(UUID taskId, UUID scenarioId) {
        var sqlStatement = """
            INSERT INTO import_data (import_type, file_row_id, match_step, match_step_name, task_id,
                record_id, object_type, dedupe_key, group_size, error, selected)
            SELECT 'Update', frk.file_row_id, 1, 'Match Step', frk.task_id, gk.record_id, gk.object_type,
                gk.full_key, 1, false, true
            FROM file_row_key frk INNER JOIN generated_key gk
                    ON gk.task_id = frk.task_id AND gk.scenario_id = frk.scenario_id AND gk.hashed_key = frk.hashed_key
            WHERE frk.task_id = ? AND frk.scenario_id = ?
            ORDER BY frk.file_row_id, frk.hashed_key, gk.record_id;
            DELETE FROM file_row_key WHERE task_id = ?;
            """;

        try (var preparedStatement = conn.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, taskId.toString());
            preparedStatement.setString(2, scenarioId.toString());
            preparedStatement.setString(3, taskId.toString());
            preparedStatement.execute();
            conn.commit();
        } catch (SQLException ex) {
            throw new H2ChunkNotFoundBugException("Error populating import_data table.", ex);
        }
    }

    public void truncateTable() {
        var truncateStatement = "TRUNCATE TABLE import_data";
        try (var truncateJob = conn.prepareStatement(truncateStatement)) {
            truncateJob.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            throw new H2ChunkNotFoundBugException("Error truncating import_data table.", ex);
        }
    }
    
}
