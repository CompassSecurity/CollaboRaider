/*
 * Copyright (c) 2023. PortSwigger Ltd. All rights reserved.
 *
 * This code may be used to extend the functionality of Burp Suite Community Edition
 * and Burp Suite Professional, provided that this usage does not violate the
 * license terms for those products.
 */

package ch.csnc.interaction;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PingbackTableModel extends AbstractTableModel
{
    private final List<Pingback> log;

    public PingbackTableModel()
    {
        this.log = new ArrayList<>();
    }

    @Override
    public synchronized int getRowCount()
    {
        return log.size();
    }

    @Override
    public int getColumnCount()
    {
        return 3;
    }

    @Override
    public String getColumnName(int column)
    {
        return switch (column)
        {
            case 0 -> "Timestamp";
            case 1 -> "Type";
            case 2 -> "Originating IP";
            default -> "";
        };
    }

    @Override
    public synchronized Object getValueAt(int rowIndex, int columnIndex)
    {
        Pingback entry = log.get(rowIndex);

        return switch (columnIndex)
        {
            case 0 -> entry.interaction.timeStamp().toString();
            case 1 -> entry.interaction.type().toString();
            case 2 -> entry.interaction.clientIp().toString();
            default -> "";
        };
    }

    public synchronized void add(Pingback entry)
    {
        int index = log.size();
        log.add(entry);
        fireTableRowsInserted(index, index);
    }

    public synchronized Pingback get(int rowIndex)
    {
        return log.get(rowIndex);
    }
}