package com.smartstar.christopherjohnson.asapscouting;

/**
 * Created by christopher.johnson on 11/24/16.
 *
 * Like an Exception, but much less serious;
 * an Oops means the app has stumbled over an internal
 * process, but has recovered from it.
 *
 * Most of the code is from the {@link Error Error} class,
 */

public class Oops extends Throwable {

    public Oops() {
        super();
    }

    public Oops(String message) {
        super(message);
    }

    public Oops(String message, Throwable cause) {
        super(message, cause);
    }

    public Oops(Throwable cause) {
        super(cause);
    }

    protected Oops(String message, Throwable cause,
                   boolean enableSuppression,
                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
