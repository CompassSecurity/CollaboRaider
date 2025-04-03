package ch.csnc.gui.settings;

import burp.api.montoya.core.HighlightColor;

import javax.swing.*;
import java.awt.*;

public class ProxySettingsPanel extends SettingsPanel {
    ProxySettingsPanel() {
        // Set title and create basic constraints
        super("Proxy Settings");

        // Highlight color
        String highlightColorTooltip = "Select color to highlight requests that caused a pingback in the Proxy tab.";
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, bottomMargin, 0);
        JLabel highlightColorLabel = new JLabel("Highlight color");
        highlightColorLabel.setToolTipText(highlightColorTooltip);
        add(highlightColorLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, leftMargin, bottomMargin, 0);
        JComboBox<HighlightColor> highlightColorSelector = new JComboBox<>(HighlightColor.values());
        highlightColorSelector.setToolTipText(highlightColorTooltip);
        add(highlightColorSelector, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(new JCheckBox("Mark requests with comment"), gbc);
    }
}
