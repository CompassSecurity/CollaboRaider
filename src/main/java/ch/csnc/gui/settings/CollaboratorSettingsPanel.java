package ch.csnc.gui.settings;

import ch.csnc.settings.SettingsModel;

import javax.swing.*;
import java.awt.*;

public class CollaboratorSettingsPanel extends AbstractSettingsPanel {
    public CollaboratorSettingsPanel(SettingsModel settingsModel) {
        // Set title and create basic constraints
        super("Collaborator Settings");

        // Polling interval
        String pollingIntervalTooltip = "Select polling interval for Collaborator.";
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        JLabel pollingIntervalLabel = new JLabel("Polling Interval");
        pollingIntervalLabel.setToolTipText(pollingIntervalTooltip);
        add(pollingIntervalLabel, gbc);

        JLabel pollingIntervalChangedLabel = new JLabel("""
                                                        <html>
                                                        <i style="color:blue;">
                                                        The new interval will take effect after reloading the extension.
                                                        </i>
                                                        </html>
                                                        """);
        pollingIntervalChangedLabel.putClientProperty("html.disable", null);
        pollingIntervalChangedLabel.setVisible(false);

        gbc.gridx++;
        gbc.insets = new Insets(0, leftMargin, 15, 0);
        SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(settingsModel.getCollaboratorPollingInterval(),
                                                                       0,
                                                                       3600,
                                                                       1);
        JSpinner pollingIntervalSpinner = new JSpinner(spinnerNumberModel);
        pollingIntervalSpinner.setToolTipText(pollingIntervalTooltip);
        pollingIntervalSpinner.addChangeListener(l -> {
            if (pollingIntervalSpinner.getValue() != null) {
                settingsModel.setCollaboratorPollingInterval((Integer) pollingIntervalSpinner.getValue());
                pollingIntervalChangedLabel.setVisible(true);
            }
        });
        add(pollingIntervalSpinner, gbc);

        gbc.gridx++;
        gbc.weightx = 1;
        add(pollingIntervalChangedLabel, gbc);

        // Observed IP addresses
        String ipTooltip = "External IP addresses of your system. This is used to distinguish whether a pingback was caused by you or by an external server.";
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel ipLabel = new JLabel("Own IP Addresses");
        ipLabel.setToolTipText(ipTooltip);
        add(ipLabel, gbc);

        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, leftMargin, 5, 0);
        JLabel ipListLabel = new JLabel("<html>waiting for response...</html>");
        settingsModel.getOwnIPAddresses().addObserver((o, arg) -> {
            if (!settingsModel.getOwnIPAddresses().get().isEmpty())
                ipListLabel.setText("<html>" + settingsModel.getOwnIPAddresses().toString() + "</html>");
            else
                ipListLabel.setText("<html>waiting for response...</html>");
        });
        ipListLabel.setToolTipText(ipTooltip);
        ipListLabel.putClientProperty("html.disable", null);
        add(ipListLabel, gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, leftMargin, 0, 0);
        JButton ipRefreshButton = new JButton("Refresh");
        ipRefreshButton.setToolTipText("Refresh external IP addresses.");
        // Wrap in new thread since network operations cannot be performed in the UI thread
        ipRefreshButton.addActionListener(e -> new Thread(settingsModel::sendCheckIpPayload).start());
        add(ipRefreshButton, gbc);

    }
}