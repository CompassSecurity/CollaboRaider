package ch.csnc.gui.settings;

import burp.api.montoya.core.HighlightColor;
import ch.csnc.gui.GBC;
import ch.csnc.settings.SettingsModel;

import javax.swing.*;

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
        String commentCheckBoxTooltip = "Add comment to requests that caused a pingback in the Proxy tab.";
        JCheckBox commentCheckBox = new JCheckBox("Mark requests with comment");
        commentCheckBox.setSelected(settingsModel.getCommentsEnabled());
        commentCheckBox.addActionListener(e -> settingsModel.setCommentsEnabled(commentCheckBox.isSelected()));
        commentCheckBox.setToolTipText(commentCheckBoxTooltip);

        // Assemble layout

        // First row: highlight color
        add(highlightColorLabel,
            new GBC(0, 0)
                    .setMargin(0, 0, bottomMargin, 0));

        add(highlightColorSelector,
            new GBC(0, 1)
                    .setWeights(0, 1)
                    .setMargin(0, leftMargin, bottomMargin, 0));

        // Second row: comment checkbox
        add(commentCheckBox,
            new GBC(1, 0)
                    .setSize(1, 2)
                    .setWeights(1, 1));
    }
}
