package ch.csnc.ui;

import burp.api.montoya.MontoyaApi;
import ch.csnc.payload.Payload;
import ch.csnc.payload.PayloadType;
import ch.csnc.payload.PayloadsTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PayloadsTab extends JPanel {
    public final String TAB_TITLE = "Payloads";
    private final PayloadsTableModel payloadsTableModel;
    private final MontoyaApi montoyaApi;
    private final Frame suiteFrame;
    JTable table;
    private JDialog dialog;

    public PayloadsTab(MontoyaApi montoyaApi, PayloadsTableModel payloadsTableModel) {
        this.payloadsTableModel = payloadsTableModel;
        this.montoyaApi = montoyaApi;

        suiteFrame = montoyaApi.userInterface().swingUtils().suiteFrame();

        setLayout(new BorderLayout());

        add(createButtonPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);

    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(this::onClickAddButton);
        buttonPanel.add(addButton);

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(this::onClickRemoveButton);
        buttonPanel.add(removeButton);

        JButton importButton = new JButton("Import");
        buttonPanel.add(importButton);

        JButton exportButton = new JButton("Export");
        buttonPanel.add(exportButton);

        return buttonPanel;
    }

    private void onClickRemoveButton(ActionEvent e) {
        int[] selectedRows = table.getSelectedRows();
        montoyaApi.logging().logToOutput("Removing " + selectedRows.length + " payloads");
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(suiteFrame, "No payload selected", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            int confirm = JOptionPane.showOptionDialog(suiteFrame, "Delete selected payload?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (confirm == JOptionPane.YES_OPTION) {
                for (int selectionIndex = selectedRows.length - 1; selectionIndex >= 0; selectionIndex--) {
                    payloadsTableModel.remove(selectedRows[selectionIndex]);
                }
            }
        }
        table.clearSelection();
    }

    private void onClickAddButton(ActionEvent e) {
        showAddDialog();
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog(suiteFrame, "Add new payload", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(suiteFrame);

        // Create a panel to hold the content
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new GridLayout(6, 2));

        // Payload location
        JLabel payloadLabel = new JLabel("Location:");
        ButtonGroup bG = new ButtonGroup();
        JRadioButton radioButton1 = new JRadioButton(PayloadType.PARAM.label);
        JRadioButton radioButton2 = new JRadioButton(PayloadType.HEADER.label);
        bG.add(radioButton1);
        bG.add(radioButton2);

        // Key (URL Parameter name or Header name)
        JLabel keyLabel = new JLabel("Key:");
        JTextField keyField = new JTextField(15);

        // Value (Payload to be inserted)
        JLabel valueLabel = new JLabel("Value:");
        JTextField valueField = new JTextField(15);
        JLabel infoLabel = new JLabel("Use %s as placeholder for Collaborator URL, eg. http://%s/file.xml.\nUse %h as placeholder for the current host.");

        // Save button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            PayloadType payloadType = null;
            if (radioButton1.isSelected()) {
                payloadType = PayloadType.PARAM;
            } else if (radioButton2.isSelected()) {
                payloadType = PayloadType.HEADER;
            }
            Payload payload = new Payload(Boolean.TRUE, payloadType, keyField.getText(), valueField.getText());
            payloadsTableModel.add(payload);
            dialog.dispose(); // Close the dialog
        });

        // Create a close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> {
            dialog.dispose(); // Close the dialog
        });


        dialogPanel.add(payloadLabel);
        dialogPanel.add(radioButton1);
        dialogPanel.add(new JLabel(""));
        dialogPanel.add(radioButton2);

        dialogPanel.add(keyLabel);
        dialogPanel.add(keyField);

        dialogPanel.add(valueLabel);
        dialogPanel.add(valueField);
        dialogPanel.add(new JLabel(""));
        dialogPanel.add(infoLabel);


        dialogPanel.add(saveButton);
        dialogPanel.add(closeButton);

        dialog.getContentPane().add(dialogPanel);
        dialog.setVisible(true);
    }

    private JScrollPane createTablePanel() {
        table = new JTable(payloadsTableModel);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setAutoCreateRowSorter(true);

        return new JScrollPane(table);
    }
}
