package ch.csnc.ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SettingsTab extends JPanel {
    public final String TAB_TITLE = "Settings";

    public SettingsTab() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(new CollaboratorSettingsPanel());
        add(new InteractionsSettingsPanel());
    }

    private class CollaboratorSettingsPanel extends JPanel {
        CollaboratorSettingsPanel() {
            setBorder(new CompoundBorder(new TitledBorder("Collaborator Settings"), new EmptyBorder(8, 0, 0, 0)));

            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            gbc.gridx = 0;
            gbc.gridy = 0;

            gbc.anchor = GridBagConstraints.NORTHWEST;

            add(new JLabel("Polling Interval"), gbc);
            gbc.gridy++;
            add(new JLabel("IP Address"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            add(new JTextField(10), gbc);
            gbc.gridy++;

            add(new JTextField(20), gbc);

            gbc.weightx = 1;
            gbc.weighty = 1;
        }
    }

    private class InteractionsSettingsPanel extends JPanel {
        InteractionsSettingsPanel() {
            setLayout(new GridBagLayout());
            setBorder(new CompoundBorder(new TitledBorder("Interactions"), new EmptyBorder(8, 0, 0, 0)));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 0, 4);
            gbc.anchor = GridBagConstraints.WEST;

            add((new JRadioButton("None")), gbc);
            gbc.gridy++;
            add((new JRadioButton("Database: ")), gbc);
        }
    }
}
