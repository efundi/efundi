package edu.nwu.sakai.studentlink.server;

@SuppressWarnings("serial")
public class ConnectionNotEstablishedException extends Exception {

    public ConnectionNotEstablishedException(String message, Throwable cause) {
        super(message, cause);
    }
}