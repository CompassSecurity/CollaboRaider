package ch.csnc.pingback;

import burp.api.montoya.persistence.PersistedObject;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PingbackTableModel extends AbstractTableModel {
    private final List<Pingback> log;
    private final PersistedObject persistedObject;
    private final String KEY_PINGBACK_NUM_ROWS = "PREFERENCES_KEY_PINGBACK_NUM_ROWS";
    private final String KEY_PINGBACK_ROW = "KEY_PINGBACK_ROW_";

    String[] columnNames = {
            "Time",                 // 0
            "Pingback Type",        // 1
            "Collaborator Payload", // 2
            "Source IP Address",    // 3
            "Payload Type",         // 4
            "Payload Target"        // 5
    };

    public PingbackTableModel(PersistedObject persistedObject) {
        this.persistedObject = persistedObject;
        // Initialize table model with persistence
        if (persistedObject.getInteger(KEY_PINGBACK_NUM_ROWS) != null) {
            int numRows = persistedObject.getInteger(KEY_PINGBACK_NUM_ROWS);
            log = new ArrayList<>(numRows);
            for (int i = 0; i < numRows; ++i) {
                PersistedObject object = persistedObject.getChildObject(KEY_PINGBACK_ROW + i);
                Pingback pingback = Pingback.fromPersistence(object);
                log.add(i, pingback);
            }
        } else {
            this.log = new ArrayList<>();
        }
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

        // Add to table
        log.add(entry);

        // Add to persistence
        int numRows = 0;
        if (persistedObject.getInteger(KEY_PINGBACK_NUM_ROWS) != null) {
            numRows = persistedObject.getInteger(KEY_PINGBACK_NUM_ROWS);
        }
        persistedObject.setChildObject(KEY_PINGBACK_ROW + numRows, entry.toPersistence());
        persistedObject.setInteger(KEY_PINGBACK_NUM_ROWS, ++numRows);

        // Signal GUI
        fireTableRowsInserted(index, index);
    }

    public synchronized void clear() {
        log.clear();
        persistedObject.deleteInteger(KEY_PINGBACK_NUM_ROWS);
        fireTableDataChanged();
    }

    public synchronized Pingback get(int rowIndex) {
        return log.get(rowIndex);
    }
}