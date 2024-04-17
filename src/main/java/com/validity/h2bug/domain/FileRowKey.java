/*
 * Copyright (c) 2024 Validity Inc.
 * All rights reserved.
 */

package com.validity.h2bug.domain;

import lombok.*;

import java.util.UUID;

@Data
@Builder
public class FileRowKey {

    private UUID id;
    private UUID taskId;
    private Long fileRowId;
    private UUID scenarioId;
    private String hashedKey;

}
