package ch.csnc.gui.settings;

import burp.api.montoya.core.HighlightColor;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SettingsTab extends JPanel {
    public final String TAB_TITLE = "Settings";

    public SettingsTab() {
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
        add(new CollaboratorSettingsPanel(), gbc);

        gbc.gridy++;
        add(new InteractionsSettingsPanel(), gbc);

        gbc.gridy++;
        add(new ProxySettingsPanel(), gbc);

        gbc.gridy++;
        add(new ExportSettingsPanel(), gbc);

        gbc.gridy++;
        gbc.weighty = 1; // Add weight to last element so that everything is shifted towards the anchor point
        add(new AboutPanel(), gbc);
    }
}
