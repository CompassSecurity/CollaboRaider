package ch.csnc.gui.settings;

import burp.api.montoya.core.HighlightColor;
import ch.csnc.settings.SettingsModel;

import javax.swing.*;
import java.awt.*;

public class ProxySettingsPanel extends AbstractSettingsPanel {
    public ProxySettingsPanel(SettingsModel settingsModel) {
        // Set title and create basic constraints
        super("Proxy Settings");

        // Highlight color
        String highlightColorTooltip = "Select color to highlight requests that caused a pingback in the Proxy tab.";
        JLabel highlightColorLabel = new JLabel("Highlight color");
        highlightColorLabel.setToolTipText(highlightColorTooltip);

        JComboBox<HighlightColor> highlightColorSelector = new JComboBox<>(HighlightColor.values());
        highlightColorSelector.setToolTipText(highlightColorTooltip);
        highlightColorSelector.setSelectedItem(settingsModel.getProxyHighlightColor());
        highlightColorSelector.addActionListener(e -> {
            if (highlightColorSelector.getSelectedItem() != null)
                settingsModel.setProxyHighlightColor((HighlightColor) highlightColorSelector.getSelectedItem());
        });

        // Comments
        String commentCheckBoxTooltip = "Add comment to request that caused a pingback in the Proxy tab.";
        JCheckBox commentCheckBox = new JCheckBox("Mark requests with comment");
        commentCheckBox.setSelected(settingsModel.getCommentsEnabled());
        commentCheckBox.addActionListener(e -> settingsModel.setCommentsEnabled(commentCheckBox.isSelected()));
        commentCheckBox.setToolTipText(commentCheckBoxTooltip);

        // Assemble layout
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, bottomMargin, 0);
        add(highlightColorLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, leftMargin, bottomMargin, 0);
        add(highlightColorSelector, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(commentCheckBox, gbc);
    }
}
