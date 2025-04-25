package ch.csnc.gui;

import ch.csnc.gui.settings.*;
import ch.csnc.settings.SettingsModel;

import javax.swing.*;
import java.awt.*;

public class SettingsTab extends JPanel {
    public final String TAB_TITLE = "Settings";

    public SettingsTab(SettingsModel settingsModel) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Align to top-left corner
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Stretch items horizontally
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Define margins
        gbc.insets = new Insets(15, 10, 0, 10);

        gbc.gridy = 0;
        add(new CollaboratorSettingsPanel(settingsModel), gbc);

        gbc.gridy++;
        add(new InteractionsSettingsPanel(settingsModel), gbc);

        gbc.gridy++;
        add(new ProxySettingsPanel(settingsModel), gbc);

        // not yet implemented
        // TODO: add import/export functionality
        // gbc.gridy++;
        // add(new ExportSettingsPanel(settingsModel), gbc);

        gbc.gridy++;
        gbc.weighty = 1; // Add weight to last element so that everything is shifted towards the anchor point
        add(new AboutPanel(settingsModel), gbc);
    }
}
