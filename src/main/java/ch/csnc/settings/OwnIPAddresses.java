package ch.csnc.settings;

import burp.api.montoya.collaborator.CollaboratorPayload;

import java.util.*;
import java.util.stream.Collectors;

public class OwnIPAddresses extends Observable {
    private Set<String> ipAddresses = new HashSet<>();

    public void init() {
        ipAddresses = new HashSet<>();
        setChanged();
        notifyObservers();
    }

    public Set<String> get() {
        return ipAddresses;
    }

    public boolean contains(String ip) {
        return ipAddresses.contains(ip);
    }

    public void add(String value) {
        this.ipAddresses.add(value);
        setChanged();
        notifyObservers();
    }

    public String toString() {
        return String.join(", ", ipAddresses.stream().sorted().collect(Collectors.joining(", ")));
    }
}
