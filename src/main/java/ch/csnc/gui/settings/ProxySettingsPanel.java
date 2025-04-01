package ch.csnc.gui.settings;

import burp.api.montoya.core.HighlightColor;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ProxySettingsPanel extends SettingsPanel {
    ProxySettingsPanel() {
        // Set title and create basic constraints
        super("Proxy Settings");

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, 10);
        add((new JLabel("Highlight color")), gbc);

        gbc.gridx++;
        gbc.weighty = 1;
        gbc.weightx = 1;
        add(new JComboBox<>(HighlightColor.values()), gbc);
    }
}
