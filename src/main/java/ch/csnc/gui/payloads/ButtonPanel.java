package ch.csnc.gui.payloads;

import javax.swing.*;
import java.awt.*;

public class ButtonPanel extends JPanel {
    public ButtonPanel(Runnable onClickAddButton
            , Runnable onClickRemoveButton
            //, Runnable onClickUpButton,
            //, Runnable onClickDownButton,
            , Runnable onClickImportButton
            , Runnable onClickExportButton
            , Runnable onClickRestoreButton
    ) {
        setLayout(new GridBagLayout());
        // buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Anchor to top-left corner
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;

        JButton addButton = new JButton("Add");
        addButton.setToolTipText("Open a dialog to add a new payload");
        addButton.addActionListener(e -> onClickAddButton.run());
        gbc.insets = new Insets(0, 0, 5, 0);
        add(addButton, gbc);
        gbc.gridy++;

        JButton removeButton = new JButton("Remove");
        removeButton.setToolTipText("Remove selected payload(s)");
        removeButton.addActionListener(e -> onClickRemoveButton.run());
        gbc.insets = new Insets(0, 0, 15, 0);
        add(removeButton, gbc);
        gbc.gridy++;

        /*
        JButton upButton = new JButton("Up");
        upButton.addActionListener(e -> onClickUpButton.run());
        gbc.insets = new Insets(0, 0, 5, 0);
        add(upButton, gbc);
        gbc.gridy++;

        JButton downButton = new JButton("Down");
        downButton.addActionListener(e -> onClickDownButton.run());
        gbc.insets = new Insets(0, 0, 15, 0);
        add(downButton, gbc);
        gbc.gridy++;
         */

        JButton importButton = new JButton("Import");
        importButton.setToolTipText("Import payloads from a file. This will replace your current payload configuration!");
        importButton.addActionListener(e -> onClickImportButton.run());
        gbc.insets = new Insets(0, 0, 5, 0);
        add(importButton, gbc);
        gbc.gridy++;

        JButton exportButton = new JButton("Export");
        exportButton.setToolTipText("Export current payloads from a file.");
        exportButton.addActionListener(e -> onClickExportButton.run());
        gbc.insets = new Insets(0, 0, 15, 0);
        add(exportButton, gbc);
        gbc.gridy++;

        JButton restoreButton = new JButton("Restore Defaults");
        restoreButton.setToolTipText("Restore default settings. This will replace your current payload configuration!");
        restoreButton.addActionListener(e -> onClickRestoreButton.run());
        gbc.insets = new Insets(0, 0, 15, 0);
        add(restoreButton, gbc);
        gbc.gridy++;
    }
}
