/*
 * Copyright (c) 2023. PortSwigger Ltd. All rights reserved.
 *
 * This code may be used to extend the functionality of Burp Suite Community Edition
 * and Burp Suite Professional, provided that this usage does not violate the
 * license terms for those products.
 */

package ch.csnc.interaction;

import javax.swing.table.AbstractTableModel;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PingbackTableModel extends AbstractTableModel {
    private final List<Pingback> log;

    String[] columnNames = {
            "Time",                 // 0
            "Pingback Type",        // 1
            "Collaborator Payload", // 2
            "Source IP Address",    // 3
            "Payload Type",         // 4
            "Payload Target"        // 5
    };

    public PingbackTableModel() {
        this.log = new ArrayList<>();
    }

    @Override
    public synchronized int getRowCount() {
        return log.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public synchronized Object getValueAt(int rowIndex, int columnIndex) {
        Pingback entry = log.get(rowIndex);

        // could this be somehow moved to a list? List of functions that consume Pingback and return String?
        return switch (columnIndex) {
            case 0 -> entry.getLocalTimestamp();
            case 1 -> entry.getPingbackType();
            case 2 -> entry.getInteractionId();
            case 3 -> entry.getInteractionClientIp();
            case 4 -> entry.getPayloadType();
            case 5 -> entry.getPayloadKey();
            default -> "";
        };
    }

    public synchronized void add(Pingback entry) {
        int index = log.size();
        log.add(entry);
        fireTableRowsInserted(index, index);
    }

    public synchronized Pingback get(int rowIndex) {
        return log.get(rowIndex);
    }
}