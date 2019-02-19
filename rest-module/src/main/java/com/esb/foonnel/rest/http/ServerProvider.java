package com.esb.foonnel.rest.http;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides a server for the given host and port.
 */
public class ServerProvider {

    private Map<KeyEntry,RESTServer> serverMap = new ConcurrentHashMap<>();

    public RESTServer get(String hostname, int port) {
        return serverMap.getOrDefault(new KeyEntry(hostname, port), new RESTServer(port, hostname));
    }

    private class KeyEntry {
        String hostname;
        int port;

        KeyEntry(String hostname, int port) {
            this.hostname = hostname;
            this.port = port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KeyEntry keyEntry = (KeyEntry) o;
            return port == keyEntry.port &&
                    hostname.equals(keyEntry.hostname);
        }

        @Override
        public int hashCode() {
            return Objects.hash(hostname, port);
        }
    }
}
