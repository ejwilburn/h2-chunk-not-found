/*
 * Copyright (c) 2024 Validity Inc.
 * All rights reserved.
 */

package com.validity.h2bug.exception;

public class H2ChunkNotFoundBugException extends RuntimeException {

    public H2ChunkNotFoundBugException(String errorMessage) {
        super(errorMessage);
    }

    public H2ChunkNotFoundBugException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}
