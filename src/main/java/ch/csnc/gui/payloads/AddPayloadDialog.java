package ch.csnc.gui.payloads;

import ch.csnc.payload.Payload;
import ch.csnc.payload.PayloadType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class AddPayloadDialog extends JDialog {
    Consumer<Payload> handler;

    JRadioButton radioButton1, radioButton2;
    JTextField keyField, valueField;

    public AddPayloadDialog(Frame suiteFrame, Consumer<Payload> handler) {
        super();

        this.handler = handler;

        setSize(500, 400);
        setLocationRelativeTo(suiteFrame);

        // Panel to hold form items
        JPanel formPanel = new JPanel();

        // Specify payload location
        JLabel payloadLabel = new JLabel("Payload location:");
        ButtonGroup buttonGroup = new ButtonGroup();
        radioButton1 = new JRadioButton(PayloadType.PARAM.label);
        radioButton2 = new JRadioButton(PayloadType.HEADER.label);
        buttonGroup.add(radioButton1);
        buttonGroup.add(radioButton2);

        // Key (URL Parameter name or Header name)
        JLabel keyLabel = new JLabel("Field name:");
        keyField = new JTextField();

        // Value (Payload to be inserted)
        JLabel valueLabel = new JLabel("Payload value:");
        valueField = new JTextField();
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
        saveButton.addActionListener(this::clickSaveButton);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        // Group buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        // Create a panel to hold the content
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BorderLayout());
        dialogPanel.add(formPanel, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(dialogPanel);
    }

    private void clickSaveButton(ActionEvent e) {
        PayloadType payloadType = null;
        if (radioButton1.isSelected()) {
            payloadType = PayloadType.PARAM;
        } else if (radioButton2.isSelected()) {
            payloadType = PayloadType.HEADER;
        }
        // Construct payload from fields and pass it to the handler
        Payload payload = new Payload(Boolean.TRUE, payloadType, keyField.getText(), valueField.getText());
        handler.accept(payload);
        //payloadsTableModel.add(payload);
        // Close dialog
        dispose();
    }
}
