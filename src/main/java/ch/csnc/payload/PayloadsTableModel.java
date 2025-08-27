package ch.csnc.payload;

import burp.api.montoya.persistence.Preferences;

import javax.swing.table.AbstractTableModel;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PayloadsTableModel extends AbstractTableModel {
    private final String[] columnNames = {
            "Active?",
            "Payload type",
            "Field name",
            "Payload value"
    };
    private final String KEY_NUM_ROWS = "PREFERENCES_KEY_PAYLOADS_NUM_ROWS";
    private final String KEY_ROW = "PREFERENCES_KEY_PAYLOADS_ROW_";
    private final Preferences preferences;
    private List<Payload> payloads;

    /**
     * Initialize the TableModel by either loading the specified payloads from settings
     * or from the file /injections if no preferences have been saved yet.
     *
     * @param preferences
     */
    public PayloadsTableModel(Preferences preferences) {
        this.preferences = preferences;
        if (preferences.getInteger(KEY_NUM_ROWS) != null) {
            loadPayloadsFromPersistence(preferences);
        } else {
            loadDefaults();
        }
    }


    @Override
    public int getRowCount() {
        return payloads.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Payload payload = payloads.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> payload.isActive();
            case 1 -> payload.getType().toString();
            case 2 -> payload.getKey();
            case 3 -> payload.getValue();
            default -> "";
        };
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        // Can't change the type for now
        return column != 1;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Payload payload = payloads.get(rowIndex);

        switch (columnIndex) {
            case 0 -> payload.setActive((Boolean) aValue);
            case 2 -> payload.setKey((String) aValue);
            case 3 -> payload.setValue((String) aValue);
        }

        preferences.setString(KEY_ROW + rowIndex, payload.toString());

        fireTableCellUpdated(rowIndex, columnIndex);
    }


    public Class<?> getColumnClass(int column) {
        return (getValueAt(0, column).getClass());
    }


    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public List<Payload> getPayloads() {
        return payloads;
    }


    public synchronized void add(Payload payload) {
        int index = payloads.size();
        payloads.add(payload);
        fireTableRowsInserted(index, index);

        // update preferences
        preferences.setString(KEY_ROW + index, payload.toString());
        preferences.setInteger(KEY_NUM_ROWS, payloads.size());
    }

    public synchronized void remove(int index) {
        payloads.remove(index);
        fireTableRowsDeleted(index, index);

        // Store all remaining elements in preferences
        int numRows = payloads.size();
        preferences.setInteger(KEY_NUM_ROWS, numRows);
        for (int i = 0; i < numRows; ++i) {
            preferences.setString(KEY_ROW + i, payloads.get(i).toString());
        }
    }

    public synchronized Payload get(int index) {
        return payloads.get(index);
    }

    public void loadPayloadsFromPersistence(Preferences preferences) {
        int numRows = preferences.getInteger(KEY_NUM_ROWS);
        payloads = new ArrayList<>(numRows);
        for (int i = 0; i < numRows; ++i) {
            String serialized = preferences.getString(KEY_ROW + i);

            // Avoid null pointer exception
            if (serialized == null)
                continue;

            // logging.logToOutput("restore " + serialized);
            Payload payload = Payload.fromString(serialized);
            payloads.add(i, payload);
        }
    }

    public void loadDefaults() {
        loadStoredPayloads(getClass().getResourceAsStream("/injections"));
    }

    public void loadStoredPayloads(InputStream inputStream) {
        payloads = new ArrayList<>();

        // Read input stream and add all items to list
        if (inputStream != null) {
            Scanner s = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\n");
            int numRows = 0;
            while (s.hasNextLine()) {
                String line = s.nextLine();

                // Comments start with ';'
                // For backwards compatibility, the prefix '# ' is also treated as a comment
                if (line.startsWith(";") || line.startsWith("# "))
                    continue;

                // Parse line
                Payload payload = Payload.fromString(line);
                payloads.add(payload);
                preferences.setString(KEY_ROW + numRows, payload.toString());
                numRows++;
            }
            preferences.setInteger(KEY_NUM_ROWS, numRows);
            s.close();
        }

    }

}
