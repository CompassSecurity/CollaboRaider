package ch.csnc.gui;

import burp.api.montoya.MontoyaApi;
import ch.csnc.payload.Payload;
import ch.csnc.payload.PayloadType;
import ch.csnc.payload.PayloadsTableModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;

public class PayloadsTab extends JPanel {
    public final String TAB_TITLE = "Payloads";
    private final PayloadsTableModel payloadsTableModel;
    private final MontoyaApi montoyaApi;
    private final Frame suiteFrame;
    JTable table;
    private JDialog dialog;
    private JLabel testLabel;

    public PayloadsTab(MontoyaApi montoyaApi, PayloadsTableModel payloadsTableModel) {
        this.payloadsTableModel = payloadsTableModel;
        this.montoyaApi = montoyaApi;

        suiteFrame = montoyaApi.userInterface().swingUtils().suiteFrame();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel buttonPanel = createButtonPanel();
        JScrollPane tablePanel = createTablePanel();
        JLabel descriptionLabel = createDescriptionPanel();

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

    private JLabel createDescriptionPanel() {
        JLabel descriptionLabel = new JLabel();
        descriptionLabel.setHorizontalAlignment(SwingConstants.LEFT);
        descriptionLabel.setVerticalAlignment(SwingConstants.TOP);

        // Explicitly allow HTML content
        descriptionLabel.putClientProperty("html.disable", null);
        // descriptionLabel.setContentType("text/html");

        descriptionLabel.setText("""
                          <html>
                          Use placeholders to define payloads:
                          <ul>
                          <li><b><tt>%s</tt></b> will be replaced with a randomly generated Collaborator URL.</li>
                          <li><b><tt>%h</tt></b> will be replaced with the <b>host</b> of the current request.</li>
                          <li><b><tt>%o</tt></b> will be replaced with the <b>origin</b> of the current request.</li>
                          <li><b><tt>%r</tt></b> will be replaced with the <b>referer</b> of the current request.</li>                          
                          </ul>
                          </html>
                          """);

        return descriptionLabel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        // buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setLayout(new GridBagLayout());
        // buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        gbc.insets = new Insets(0,0,5,0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Anchor to top-left corner
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;

        JButton addButton = new JButton("Add");
        addButton.addActionListener(this::onClickAddButton);
        buttonPanel.add(addButton, gbc);
        gbc.gridy++;

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(this::onClickRemoveButton);
        buttonPanel.add(removeButton, gbc);
        gbc.gridy++;

        JButton importButton = new JButton("Import");
        buttonPanel.add(importButton, gbc);
        gbc.gridy++;

        JButton exportButton = new JButton("Export");
        // Set y-weight of last component to 1 for vertical adjustment
        //gbc.weighty = 1.0;
        buttonPanel.add(exportButton, gbc);

        return buttonPanel;
    }

    private void onClickRemoveButton(ActionEvent e) {
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

    private void onClickAddButton(ActionEvent e) {
        showAddDialog();
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog(suiteFrame, "Add new payload", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(suiteFrame);

        // Panel to hold form items
        JPanel formPanel = new JPanel();

        // Specify payload location
        JLabel payloadLabel = new JLabel("Payload location:");
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton radioButton1 = new JRadioButton(PayloadType.PARAM.label);
        JRadioButton radioButton2 = new JRadioButton(PayloadType.HEADER.label);
        buttonGroup.add(radioButton1);
        buttonGroup.add(radioButton2);

        // Key (URL Parameter name or Header name)
        JLabel keyLabel = new JLabel("Field name:");
        JTextField keyField = new JTextField();

        // Value (Payload to be inserted)
        JLabel valueLabel = new JLabel("Payload value:");
        JTextField valueField = new JTextField();
        String labelText = """
        <html>
        Use %s as a placeholder for a generated Collaborator URL:
        <br>
        eg. <tt>http://%s/file.xml</tt>
        becomes <tt>http://randomid.collaboratorserver.com/file.xml.</tt>
        
        <br><br>
        
        Use %h as a placeholder for the host header of the original request.
        <br>
        For example, if the original request is sent to the host <tt>example.com</tt>,
        then the payload <tt>%s@%h</tt> will become <tt>example.com@randomid.collaboratorserver.com</tt>.
        </html>
        """;
        JLabel infoLabel = new JLabel(labelText);
        infoLabel.putClientProperty("html.disable", null);


        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;



        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 25, 0, 25);
        formPanel.add(payloadLabel, gbc);

        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(radioButton1, gbc);

        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 25, 5, 25);
        formPanel.add(radioButton2, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.insets = new Insets(5, 25, 5, 25);
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(keyLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(keyField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(valueLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(valueField, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15, 25, 5, 25);
        formPanel.add(infoLabel, gbc);

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

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);


        // Create a panel to hold the content
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BorderLayout());
        dialogPanel.add(formPanel, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.getContentPane().add(dialogPanel);
        dialog.setVisible(true);
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
