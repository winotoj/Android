package com.ipd10.tododb;

/**
 * Created by 1795661 on 1/18/2018.
 */

public class DataStorageException extends Exception {
    public DataStorageException() {
    }

    public DataStorageException(String message) {
        super(message);
    }

    public DataStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataStorageException(Throwable cause) {
        super(cause);
    }
}
