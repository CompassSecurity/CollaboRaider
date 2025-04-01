package ch.csnc.gui.settings;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class InteractionsSettingsPanel extends SettingsPanel {
    InteractionsSettingsPanel() {
        // Set title and create basic constraints
        super("Interactions");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 0, 4);
        gbc.anchor = GridBagConstraints.WEST;

        add((new JRadioButton("None")), gbc);
        gbc.gridy++;
        add((new JRadioButton("Database: ")), gbc);
    }
}
