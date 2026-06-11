package com.universe.mobileapi;

final class ServiceException extends RuntimeException {

    private final int status;

    ServiceException(int status, String message) {
        super(message);
        this.status = status;
    }

    int status() {
        return status;
    }
}

