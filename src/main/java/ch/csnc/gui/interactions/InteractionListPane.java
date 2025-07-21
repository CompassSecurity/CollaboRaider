package ch.csnc.gui.interactions;

import burp.api.montoya.ui.UserInterface;
import ch.csnc.pingback.Pingback;
import ch.csnc.pingback.PingbackTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class InteractionListPane extends JScrollPane {
    JTable table;
    PingbackTableModel tableModel;
    UserInterface userInterface;
    TabController tabController;

    public InteractionListPane(TabController tabController, PingbackTableModel tableModel) {
        super();

        this.tabController = tabController;
        this.userInterface = tabController.getUserInterface();
        this.tableModel = tableModel;

        JTable table = new JTable(tableModel)
        {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend)
            {
                // Convert index of selected row to index in data object
                int actualRowIndex = convertRowIndexToModel(rowIndex);
                Pingback pingback = tableModel.get(actualRowIndex);

                // Signal tab controller that the Details view should be updated
                tabController.updateDetailsView(pingback);
                super.changeSelection(rowIndex, columnIndex, toggle, extend);
            }
        };

        // Popup menu
        JPopupMenu popup = new JPopupMenu();
        JMenuItem clearInteractionsItem = new JMenuItem("Clear all interactions");
        clearInteractionsItem.addActionListener(this::clearInteractions);
        popup.add(clearInteractionsItem);
        table.setComponentPopupMenu(popup);

        table.setAutoCreateRowSorter(true);

        setViewportView(table);
    }

    private void clearInteractions(ActionEvent e) {
        // Confirm with a dialog
        int confirm = JOptionPane.showOptionDialog(userInterface.swingUtils().suiteFrame(),
                                                   "Delete all interactions?",
                                                   "Confirm",
                                                   JOptionPane.YES_NO_OPTION,
                                                   JOptionPane.QUESTION_MESSAGE,
                                                   null,
                                                   null,
                                                   null);
        // yeet
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.clear();
            tabController.hideDetailsView();
        }
    }
}
