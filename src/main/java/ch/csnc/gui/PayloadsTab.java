package ch.csnc.gui;

import burp.api.montoya.MontoyaApi;
import ch.csnc.gui.components.FileChooser;
import ch.csnc.gui.payloads.DescriptionLabel;
import ch.csnc.gui.payloads.AddPayloadDialog;
import ch.csnc.gui.payloads.ButtonPanel;
import ch.csnc.payload.Payload;
import ch.csnc.payload.PayloadsTableModel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PayloadsTab extends JPanel {
    public final String TAB_TITLE = "Payloads";
    private final PayloadsTableModel payloadsTableModel;
    private final MontoyaApi montoyaApi;
    private final Frame suiteFrame;
    JTable table;

    public PayloadsTab(MontoyaApi montoyaApi, PayloadsTableModel payloadsTableModel) {
        this.payloadsTableModel = payloadsTableModel;
        this.montoyaApi = montoyaApi;

        suiteFrame = montoyaApi.userInterface().swingUtils().suiteFrame();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel buttonPanel = new ButtonPanel(this::onClickAddButton
                                             , this::onClickRemoveButton
                                             , this::onClickImportButton
                                             , this::onClickExportButton
                                             , this::onClickRestoreButton
                                             );
        JScrollPane tablePanel = createTablePanel();
        JLabel descriptionLabel = new DescriptionLabel();

        gbc.anchor = GridBagConstraints.FIRST_LINE_START;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(10, 10, 10, 0);
        add(buttonPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 0, 10);
        add(tablePanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(descriptionLabel, gbc);
    }

    private void onClickRemoveButton() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(suiteFrame, "No payload selected", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            int confirm = JOptionPane.showOptionDialog(suiteFrame,
                                                       "Delete selected payloads?",
                                                       "Confirm",
                                                       JOptionPane.YES_NO_OPTION,
                                                       JOptionPane.QUESTION_MESSAGE,
                                                       null,
                                                       null,
                                                       null);
            if (confirm == JOptionPane.YES_OPTION) {
                for (int selectionIndex = selectedRows.length - 1; selectionIndex >= 0; selectionIndex--) {
                    payloadsTableModel.remove(selectedRows[selectionIndex]);
                }
            }
        }
        table.clearSelection();
    }

    private void onClickAddButton() {
        AddPayloadDialog addPayloadDialog = new AddPayloadDialog(suiteFrame, payloadsTableModel::add);
        addPayloadDialog.setVisible(true);
    }

    private void onClickImportButton() {
        FileChooser fileChooser;

        fileChooser = new FileChooser(this);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("collaboraider_payloads.csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("csv file", "csv"));

        try (InputStream inputStream = fileChooser.openFile()) {
            if (inputStream != null) {
                payloadsTableModel.loadStoredPayloads(inputStream);
                payloadsTableModel.fireTableDataChanged();
            }
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(suiteFrame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void onClickExportButton() {
        FileChooser fileChooser;

        fileChooser = new FileChooser(this);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("collaboraider_payloads.csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("csv file", "csv"));

        StringBuilder output = new StringBuilder();
        for (Payload p : payloadsTableModel.getPayloads()) {
            output.append(p.toString())
                  .append("\n");
        }

        try {
            montoyaApi.logging().logToOutput(fileChooser.saveFile(output.toString()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(suiteFrame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void onClickRestoreButton() {
        payloadsTableModel.loadDefaults();
        payloadsTableModel.fireTableDataChanged();
    }


    private JScrollPane createTablePanel() {
        table = new JTable(payloadsTableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumn column1 = table.getColumnModel().getColumn(0);
        column1.setPreferredWidth(60);

        TableColumn column2 = table.getColumnModel().getColumn(1);
        column2.setPreferredWidth(120);

        TableColumn column3 = table.getColumnModel().getColumn(2);
        column3.setPreferredWidth(160);


        // Add component listener to adjust the last column width
        scrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int totalWidth = scrollPane.getWidth();
                int usedWidth = column1.getPreferredWidth() + column2.getPreferredWidth() + column3.getPreferredWidth();
                int buffer = 2;
                int remainingWidth = totalWidth - usedWidth - buffer;

                TableColumn column4 = table.getColumnModel().getColumn(3);
                column4.setPreferredWidth(Math.max(remainingWidth, 300));
            }
        });

        return scrollPane;
    }
}
