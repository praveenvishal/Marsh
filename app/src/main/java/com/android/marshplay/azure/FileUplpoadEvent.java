package com.android.marshplay.azure;

public class FileUplpoadEvent {
    STATUS status;

    public FileUplpoadEvent(STATUS status) {
        this.status = status;
    }

    public static FileUplpoadEvent success() {
        return new FileUplpoadEvent(STATUS.SUCCESS);
    }

    public static FileUplpoadEvent error() {
        return new FileUplpoadEvent(STATUS.ERROR);
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public enum STATUS {
        SUCCESS, ERROR
    }
}
