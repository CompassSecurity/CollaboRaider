package ch.csnc.gui.settings;

import burp.api.montoya.scanner.audit.issues.AuditIssueSeverity;
import ch.csnc.gui.GBC;
import ch.csnc.settings.SettingsModel;

import javax.swing.*;
import java.awt.event.ActionListener;

public class InteractionsSettingsPanel extends AbstractSettingsPanel {
    int test = 0;

    public InteractionsSettingsPanel(SettingsModel settingsModel) {
        // Set title and create basic constraints
        super("Interactions");

        // Handle interactions from own IP
        JLabel actionsLabel = new JLabel("Action for pingback from own IP");
        ButtonGroup actionButtonGroup = new ButtonGroup();
        ActionListener l = e -> settingsModel.setActionForOwnIP(e.getActionCommand());

        JRadioButton continueButton = new JRadioButton("Continue");
        continueButton.setSelected(settingsModel.getActionForOwnIP() == SettingsModel.ActionForOwnIP.CONTINUE);
        continueButton.setActionCommand(String.valueOf(SettingsModel.ActionForOwnIP.CONTINUE));
        continueButton.addActionListener(l);
        actionButtonGroup.add(continueButton);

        JRadioButton reduceButton = new JRadioButton("Continue, but reduce severity to LOW");
        reduceButton.setSelected(settingsModel.getActionForOwnIP() == SettingsModel.ActionForOwnIP.REDUCED_RATING);
        reduceButton.setActionCommand(String.valueOf(SettingsModel.ActionForOwnIP.REDUCED_RATING));
        reduceButton.addActionListener(l);
        actionButtonGroup.add(reduceButton);

        JRadioButton dropButton = new JRadioButton("Drop");
        dropButton.setSelected(settingsModel.getActionForOwnIP() == SettingsModel.ActionForOwnIP.DROP);
        dropButton.setActionCommand(String.valueOf(SettingsModel.ActionForOwnIP.DROP));
        dropButton.addActionListener(l);
        actionButtonGroup.add(dropButton);

        // Severity
        JLabel severityLabel = new JLabel("Severity ratings for pingbacks");
        JLabel severityDNSLabel = new JLabel("DNS");
        JComboBox<AuditIssueSeverity> severityDNS = new JComboBox<>(AuditIssueSeverity.values());
        severityDNS.setSelectedItem(settingsModel.getPingbackSeverityDNS());
        severityDNS.addActionListener(e -> {
            if (severityDNS.getSelectedItem() != null)
                settingsModel.setPingbackSeverityDNS((AuditIssueSeverity) severityDNS.getSelectedItem());
        });

        JLabel severityHTTPLabel = new JLabel("HTTP");
        JComboBox<AuditIssueSeverity> severityHTTP = new JComboBox<>(AuditIssueSeverity.values());
        severityHTTP.setSelectedItem(settingsModel.getPingbackSeverityHTTP());
        severityHTTP.addActionListener(e -> {
            if (severityHTTP.getSelectedItem() != null)
                settingsModel.setPingbackSeverityHTTP((AuditIssueSeverity) severityHTTP.getSelectedItem());
        });


        JLabel severitySMTPLabel = new JLabel("SMTP");
        JComboBox<AuditIssueSeverity> severitySMTP = new JComboBox<>(AuditIssueSeverity.values());
        severitySMTP.setSelectedItem(settingsModel.getPingbackSeveritySMTP());
        severitySMTP.addActionListener(e -> {
            if (severitySMTP.getSelectedItem() != null)
                settingsModel.setPingbackSeveritySMTP((AuditIssueSeverity) severitySMTP.getSelectedItem());
        });

        // Assemble layout
        add(actionsLabel,
            new GBC(0, 0)
                    .setMargin(0, 0, bottomMarginInGroup, 0));
        add(continueButton,
            new GBC(0, 1)
                    .setWeights(0, 1)
                    .setSize(1, 2)
                    .setMargin(0, leftMargin, bottomMarginInGroup, 0));
        add(reduceButton,
            new GBC(1, 1)
                    .setWeights(0, 1)
                    .setSize(1, 2)
                    .setMargin(0, leftMargin, bottomMarginInGroup, 0));
        add(dropButton,
            new GBC(2, 1)
                    .setWeights(0, 1)
                    .setSize(1, 2)
                    .setMargin(0, leftMargin, bottomMargin, 0));

        add(severityLabel,
            new GBC(3, 0)
                    .setWeights(1, 0)
                    .setMargin(0, 0, bottomMarginInGroup, 0));
        add(severityDNSLabel,
            new GBC(3, 1)
                    .setMargin(0, leftMargin, bottomMarginInGroup, 0));
        add(severityDNS,
            new GBC(3, 2)
                    .setWeights(0, 1)
                    .setMargin(0, leftMargin, bottomMarginInGroup, 0));
        add(severityHTTPLabel,
            new GBC(4, 1)
                    .setMargin(0, leftMargin, bottomMarginInGroup, 0));
        add(severityHTTP,
            new GBC(4, 2)
                    .setWeights(0, 1)
                    .setMargin(0, leftMargin, bottomMarginInGroup, 0));
        add(severitySMTPLabel,
            new GBC(5, 1)
                    .setMargin(0, leftMargin, bottomMarginInGroup, 0));
        add(severitySMTP,
            new GBC(5, 2)
                    .setWeights(0, 1)
                    .setMargin(0, leftMargin, 0, 0));
    }
}
