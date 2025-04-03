package ch.csnc.gui.settings;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class CollaboratorSettingsPanel extends SettingsPanel {
    CollaboratorSettingsPanel() {
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

        gbc.gridx++;
        gbc.insets = new Insets(0, leftMargin, 15, 0);
        JSpinner pollingIntervalSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 3600, 1));
        pollingIntervalSpinner.setToolTipText(pollingIntervalTooltip);
        add(pollingIntervalSpinner, gbc);

        // Observed IP addresses
        String ipTooltip = "External IP addresses of your system. This is used to detect whether a pingback was caused by you or an external server.";
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel ipLabel = new JLabel("Own IP Addresses");
        ipLabel.setToolTipText(ipTooltip);
        add(ipLabel, gbc);

        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0, leftMargin, 5, 0);
        JLabel ipListLabel = new JLabel("<html>" +
                                            "(HTTP) 178.197.206.195, " +
                                            "(DNS) 193.135.111.5, " +
                                            "(DNS) 193.135.111.5" +
                                            "</html>");
        ipListLabel.setToolTipText(ipTooltip);
        ipListLabel.putClientProperty("html.disable", null);
        add(ipListLabel, gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, leftMargin, 0, 0);
        JButton ipRefreshButton = new JButton("Refresh");
        ipRefreshButton.setToolTipText("Refresh external IP addresses.");
        add(ipRefreshButton, gbc);

    }
}