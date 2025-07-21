package ch.csnc.gui.payloads;

import ch.csnc.payload.Payload;
import ch.csnc.payload.PayloadType;
import ch.csnc.settings.SettingsModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class AddPayloadDialog extends JDialog {
    Consumer<Payload> handler;

    JRadioButton radioButtonPositionParameter, radioButtonPositionHeader;
    JTextField keyField, valueField;

    public AddPayloadDialog(Frame suiteFrame, Consumer<Payload> handler, SettingsModel settingsModel) {
        super(suiteFrame, "Add new payload");

        this.handler = handler;

        setSize(600, 400);
        setLocationRelativeTo(suiteFrame);

        // Panel to hold form items
        JPanel formPanel = new JPanel();

        // Specify payload location
        JLabel payloadLabel = new JLabel("Payload location:");
        String payloadTooltip = "Specify where the payload should be applied.";
        payloadLabel.setToolTipText(payloadTooltip);
        ButtonGroup buttonGroup = new ButtonGroup();
        radioButtonPositionParameter = new JRadioButton(PayloadType.PARAM.label);
        radioButtonPositionParameter.setToolTipText(payloadTooltip + "\n-> Create payload as URL parameter.");
        radioButtonPositionHeader = new JRadioButton(PayloadType.HEADER.label);
        radioButtonPositionHeader.setToolTipText(payloadTooltip + "\n-> Create payload as HTTP Header.");
        radioButtonPositionHeader.setSelected(true);
        buttonGroup.add(radioButtonPositionParameter);
        buttonGroup.add(radioButtonPositionHeader);

        // Key (URL Parameter name or Header name)
        JLabel keyLabel = new JLabel("Field name:");
        String keyTooltip = "Name of the URL parameter or HTTP Header field, eg. User-Agent";
        keyLabel.setToolTipText(keyTooltip);
        keyField = new JTextField();
        keyField.setToolTipText(keyTooltip);

        // Value (Payload to be inserted)
        JLabel valueLabel = new JLabel("Payload value:");
        String valueTooltip = "Value of the parameter or header. This should contain a Collaborator URL placeholder.";
        valueLabel.setToolTipText(valueTooltip);
        valueField = new JTextField();
        valueField.setToolTipText(valueTooltip);

        // Helper text
        String labelText = """
        <html>
        Use <tt>%%s</tt> as a placeholder for a generated Collaborator URL:
        <br>
        eg. <tt>http://%%s/file.xml</tt>
        becomes <tt>http://%s/file.xml.</tt>
        
        <br><br>
        
        Use <tt>%%h</tt> as a placeholder for the Host header of the original request.
        <br>
        For example, if the original request contains the header <tt>Host: example.com</tt>,
        then the payload <tt>%%h:%%s</tt> will become <tt>example.com:%s</tt>.
        
        <br><br>
        
        Similarly, the placeholder <tt>%%o</tt> can be used for the Origin header, and <tt>%%r</tt> for the Referer header.
        </html>
        """.formatted(settingsModel.getCheckIpPayload().toString(), settingsModel.getCheckIpPayload().toString());
        JLabel infoLabel = new JLabel(labelText);
        infoLabel.putClientProperty("html.disable", null);
        infoLabel.setToolTipText("got it?");


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
        formPanel.add(radioButtonPositionParameter, gbc);

        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 25, 5, 25);
        formPanel.add(radioButtonPositionHeader, gbc);

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
        if (radioButtonPositionParameter.isSelected()) {
            payloadType = PayloadType.PARAM;
        } else if (radioButtonPositionHeader.isSelected()) {
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
