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

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(new JLabel("Polling Interval"), gbc);

        gbc.gridx++;
        gbc.insets = new Insets(0, 10, 0, 0);
        add(new JTextField(30), gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(new JLabel("IP Address"), gbc);

        gbc.gridx++;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 10, 0, 0);
        add(new JTextField(30), gbc);
    }
}