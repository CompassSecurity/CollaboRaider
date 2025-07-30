package ch.csnc.settings;


import java.util.*;
import java.util.stream.Collectors;

public class OwnIPAddresses {
    private Set<String> ipAddresses = new HashSet<>();
    private Runnable runnable;

    public void init() {
        ipAddresses = new HashSet<>();
        notifyListener();
    }

    public Set<String> get() {
        return ipAddresses;
    }

    public boolean contains(String ip) {
        return ipAddresses.contains(ip);
    }

    public void add(String value) {
        this.ipAddresses.add(value);
        notifyListener();
    }

    public void addCallback(Runnable runnable) {
        this.runnable = runnable;
        notifyListener();
    }

    private void notifyListener() {
        if (runnable != null) runnable.run();
    }

    public String toString() {
        return String.join(", ", ipAddresses.stream().sorted().collect(Collectors.joining(", ")));
    }
}
