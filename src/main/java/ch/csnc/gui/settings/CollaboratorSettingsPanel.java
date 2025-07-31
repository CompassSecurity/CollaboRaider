package ch.csnc.gui.settings;

import ch.csnc.gui.GBC;
import ch.csnc.settings.SettingsModel;

import javax.swing.*;
import java.awt.*;

public class CollaboratorSettingsPanel extends AbstractSettingsPanel {
    public CollaboratorSettingsPanel(SettingsModel settingsModel) {
        // Set title and create basic constraints
        super("Collaborator Settings");

        // Polling interval
        String pollingIntervalTooltip = "Select polling interval for Collaborator.";
        JLabel pollingIntervalLabel = new JLabel("Polling Interval");
        pollingIntervalLabel.setToolTipText(pollingIntervalTooltip);

        // Label that is shown if polling interval is changed
        JLabel pollingIntervalChangedLabel = new JLabel("""
                                                        <html>
                                                        <i style="color:blue;">
                                                        &nbsp;&nbsp;The new interval will take effect after reloading the extension.
                                                        </i>
                                                        </html>
                                                        """);
        pollingIntervalChangedLabel.putClientProperty("html.disable", null);
        pollingIntervalChangedLabel.setVisible(false);

        // Spinner to select polling interval
        SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(settingsModel.getCollaboratorPollingInterval(),
                                                                       1,
                                                                       3600,
                                                                       1);
        JSpinner pollingIntervalSpinner = new JSpinner(spinnerNumberModel);
        pollingIntervalSpinner.setToolTipText(pollingIntervalTooltip);

        // Update settings and label if value is changed
        pollingIntervalSpinner.addChangeListener(l -> {
            if (pollingIntervalSpinner.getValue() != null) {
                settingsModel.setCollaboratorPollingInterval((Integer) pollingIntervalSpinner.getValue());
                pollingIntervalChangedLabel.setVisible(true);
            }
        });

        // Create new panel to combine spinner and note label
        JPanel intervalPanel = new JPanel();
        intervalPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        intervalPanel.add(pollingIntervalSpinner);
        intervalPanel.add(pollingIntervalChangedLabel);

        // Collaborator server info
        String serverTooltip = "Address of the Collaborator server that is currently in use.";
        JLabel serverLabel = new JLabel("Collaborator server");
        serverLabel.setToolTipText(serverTooltip);

        JLabel serverAddress = new JLabel(settingsModel.getCollaboratorAddress());
        serverAddress.setToolTipText(serverTooltip);

        // External IPs
        String ipTooltip = "External IP addresses of your system. This is used to distinguish whether a pingback was caused by you or by an external server.";
        JLabel ipLabel = new JLabel("Own IP addresses");
        ipLabel.setToolTipText(ipTooltip);
        JLabel ipListLabel = new JLabel("<html>waiting for response...</html>");
        settingsModel.getOwnIPAddresses().addCallback(() -> {
            if (!settingsModel.getOwnIPAddresses().get().isEmpty())
                ipListLabel.setText("<html>" + settingsModel.getOwnIPAddresses().toString() + "</html>");
            else
                ipListLabel.setText("<html>waiting for response...</html>");
        });
        ipListLabel.setToolTipText(ipTooltip);
        ipListLabel.putClientProperty("html.disable", null);

        JButton ipRefreshButton = new JButton("Refresh");
        ipRefreshButton.setToolTipText("Refresh external IP addresses.");
        // Wrap in new thread since network operations cannot be performed in the UI thread
        ipRefreshButton.addActionListener(e -> new Thread(settingsModel::sendCheckIpPayload).start());

        // Assemble layout

        // First row: polling interval
        add(pollingIntervalLabel, new GBC(0, 0)
                .setMargin(0, 0, bottomMargin, 0));

        add(intervalPanel, new GBC(0, 1)
                .setMargin(0, leftMargin, bottomMargin, 0)
                .setWeights(0, 1));


        // Second row: server address
        add(serverLabel, new GBC(2, 0)
                .setMargin(0, 0, bottomMargin, 0));

        add(serverAddress, new GBC(2, 1)
                .setMargin(0, leftMargin, bottomMargin, 0));


        // Third row: observed IP addresses
        add(ipLabel, new GBC(3, 0)
                .setMargin(0, 0, bottomMarginInGroup, 0));

        add(ipListLabel, new GBC(3, 1)
                .setMargin(0, leftMargin, bottomMarginInGroup, 0));

        add(ipRefreshButton, new GBC(4, 1)
                .setMargin(0, leftMargin, 0, 0)
                .setWeights(1, 1));

    }
}