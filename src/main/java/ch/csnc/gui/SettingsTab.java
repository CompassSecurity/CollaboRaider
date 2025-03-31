package ch.csnc.gui;

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

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 0, 10);

        gbc.gridy = 0;

        add(new CollaboratorSettingsPanel(), gbc);

        gbc.gridy++;
        add(new InteractionsSettingsPanel(), gbc);

        gbc.gridy++;
        gbc.weightx = 1;
        gbc.weighty = 1;
        add(new ProxySettingsPanel(), gbc);
    }

    private class CollaboratorSettingsPanel extends JPanel {
        CollaboratorSettingsPanel() {
            setBorder(new CompoundBorder(new TitledBorder("Collaborator Settings"), new EmptyBorder(8,8,8,8)));

            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.NONE;

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

    private class InteractionsSettingsPanel extends JPanel {
        InteractionsSettingsPanel() {
            setLayout(new GridBagLayout());
            setBorder(new CompoundBorder(new TitledBorder("Interactions"), new EmptyBorder(8, 8, 8, 8)));
            GridBagConstraints gbc = new GridBagConstraints();

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

    private class ProxySettingsPanel extends JPanel {
        ProxySettingsPanel() {
            setLayout(new GridBagLayout());
            setBorder(new CompoundBorder(new TitledBorder("Proxy"), new EmptyBorder(8, 8, 8, 8)));
            GridBagConstraints gbc = new GridBagConstraints();

            gbc.anchor = GridBagConstraints.WEST;

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
}
