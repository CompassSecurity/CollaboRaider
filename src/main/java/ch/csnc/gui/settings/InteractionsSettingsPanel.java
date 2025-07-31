package ch.csnc.gui.settings;

import burp.api.montoya.scanner.audit.issues.AuditIssueSeverity;
import ch.csnc.gui.GBC;
import ch.csnc.settings.SettingsModel;

import javax.swing.*;
import java.awt.event.ActionListener;

public class InteractionsSettingsPanel extends AbstractSettingsPanel {

    public InteractionsSettingsPanel(SettingsModel settingsModel) {
        // Set title and create basic constraints
        super("Interactions");

        // Handle interactions from own IP
        String interactionTooltip = "Decide what should happen if a pingback was received from the own system.";
        JLabel actionsLabel = new JLabel("Action for pingback from own IP");
        actionsLabel.setToolTipText(interactionTooltip);
        ButtonGroup actionButtonGroup = new ButtonGroup();
        ActionListener l = e -> settingsModel.setActionForOwnIP(e.getActionCommand());

        JRadioButton continueButton = new JRadioButton("Report");
        continueButton.setToolTipText(interactionTooltip + "\n-> Don't treat it in any special way, report it just like any other pingback.");
        continueButton.setSelected(settingsModel.getActionForOwnIP() == SettingsModel.ActionForOwnIP.CONTINUE);
        continueButton.setActionCommand(String.valueOf(SettingsModel.ActionForOwnIP.CONTINUE));
        continueButton.addActionListener(l);
        actionButtonGroup.add(continueButton);

        JRadioButton reduceButton = new JRadioButton("Report, but reduce severity to LOW");
        reduceButton.setToolTipText(interactionTooltip + "\n-> Report it just like any other pingback, but reduce the severity rating.");
        reduceButton.setSelected(settingsModel.getActionForOwnIP() == SettingsModel.ActionForOwnIP.REDUCED_RATING);
        reduceButton.setActionCommand(String.valueOf(SettingsModel.ActionForOwnIP.REDUCED_RATING));
        reduceButton.addActionListener(l);
        actionButtonGroup.add(reduceButton);

        JRadioButton dropButton = new JRadioButton("Don't report");
        dropButton.setToolTipText(interactionTooltip + "\n-> Do not report it.");
        dropButton.setSelected(settingsModel.getActionForOwnIP() == SettingsModel.ActionForOwnIP.DROP);
        dropButton.setActionCommand(String.valueOf(SettingsModel.ActionForOwnIP.DROP));
        dropButton.addActionListener(l);
        actionButtonGroup.add(dropButton);

        // Severity
        JLabel severityLabel = new JLabel("Severity ratings for pingbacks");
        String severityTooltip = "Choose how different pingbacks should be rated.";
        severityLabel.setToolTipText(severityTooltip);

        JLabel severityDNSLabel = new JLabel("DNS");
        String severityDNSTooltip = "Choose severity rating for DNS pingbacks.";
        severityDNSLabel.setToolTipText(severityDNSTooltip);

        JComboBox<AuditIssueSeverity> severityDNS = new JComboBox<>(AuditIssueSeverity.values());
        severityDNS.setToolTipText(severityDNSTooltip);
        severityDNS.setSelectedItem(settingsModel.getPingbackSeverityDNS());
        severityDNS.addActionListener(e -> {
            if (severityDNS.getSelectedItem() != null)
                settingsModel.setPingbackSeverityDNS((AuditIssueSeverity) severityDNS.getSelectedItem());
        });

        JLabel severityHTTPLabel = new JLabel("HTTP");
        String severityHTTPTooltip = "Choose severity rating for HTTP(s) pingbacks.";
        severityHTTPLabel.setToolTipText(severityHTTPTooltip);

        JComboBox<AuditIssueSeverity> severityHTTP = new JComboBox<>(AuditIssueSeverity.values());
        severityHTTP.setToolTipText(severityHTTPTooltip);
        severityHTTP.setSelectedItem(settingsModel.getPingbackSeverityHTTP());
        severityHTTP.addActionListener(e -> {
            if (severityHTTP.getSelectedItem() != null)
                settingsModel.setPingbackSeverityHTTP((AuditIssueSeverity) severityHTTP.getSelectedItem());
        });


        JLabel severitySMTPLabel = new JLabel("SMTP");
        String severitySMTPTooltip = "Choose severity rating for SMTP pingbacks.";
        severitySMTPLabel.setToolTipText(severitySMTPTooltip);

        JComboBox<AuditIssueSeverity> severitySMTP = new JComboBox<>(AuditIssueSeverity.values());
        severitySMTP.setToolTipText(severitySMTPTooltip);
        severitySMTP.setSelectedItem(settingsModel.getPingbackSeveritySMTP());
        severitySMTP.addActionListener(e -> {
            if (severitySMTP.getSelectedItem() != null)
                settingsModel.setPingbackSeveritySMTP((AuditIssueSeverity) severitySMTP.getSelectedItem());
        });

        // Assemble layout

        // First row: Action for own IP
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

        // Second row: Severity ratings
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
                    .setMargin(0, leftMargin, bottomMarginInGroup, 0));
    }
}
