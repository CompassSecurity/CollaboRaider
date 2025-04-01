package ch.csnc.payload;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class PayloadsTableModel extends AbstractTableModel {
    private final List<Payload> payloads;
    private final String[] columnNames = {
            "Active?",
            "Payload type",
            "Field name",
            "Payload value"
    };

    public PayloadsTableModel(List<Payload> payloads) {
        this.payloads = payloads;
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

        fireTableCellUpdated(rowIndex, columnIndex);
    }


    public Class<?> getColumnClass(int column) {
        return (getValueAt(0, column).getClass());
    }


    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    // Access / Modify elements

    public synchronized void add(Payload payload) {
        int index = payloads.size();
        payloads.add(payload);
        fireTableRowsInserted(index, index);
    }

    public synchronized void remove(int index) {
        payloads.remove(index);
        fireTableRowsDeleted(index, index);
    }

    public synchronized Payload get(int index) {
        return payloads.get(index);
    }

}
