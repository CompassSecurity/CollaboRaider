package ch.csnc.gui.settings;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Helper class for settings
 */
public abstract class AbstractSettingsPanel extends JPanel {
    GridBagConstraints gbc;
    int leftMargin = 15;
    int bottomMargin = 15;
    int bottomMarginInGroup = 5;

    public AbstractSettingsPanel(String name) {
        // Add border with text and padding
        setBorder(new CompoundBorder(new TitledBorder(name), new EmptyBorder(8, 8, 8, 8)));
        setLayout(new GridBagLayout());

        // Set up basic constraints
        gbc = new GridBagConstraints();
        // Align to left corner
        gbc.anchor = GridBagConstraints.WEST;
        // Don't stretch elements
        gbc.fill = GridBagConstraints.NONE;
    }
}
