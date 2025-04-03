package ch.csnc.gui.settings;

import ch.csnc.gui.GBC;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExportSettingsPanel extends SettingsPanel {
    JLabel exportLabel, importlabel;

    public ExportSettingsPanel() {
        super("Import / Export Settings");

        // Create buttons
        JButton importButton = new JButton("Import settings from file");
        JButton exportButton = new JButton("Export current settings to file");

        // Add listeners for buttons
        exportButton.addActionListener(this::clickExportButton);
        importButton.addActionListener(this::clickImportButton);

        // Assemble layout
        add(exportButton,
            new GBC(0, 0)
                    .fill(GBC.HORIZONTAL)
                    .setMargin(0, 0, bottomMarginInGroup, 0));
        add(importButton,
            new GBC(1, 0)
                    .fill(GBC.HORIZONTAL));
        // Introduce another column so that both buttons can have the same width without filling the entire window
        exportLabel = new JLabel("");
        importlabel = new JLabel("");
        add(exportLabel,
            new GBC(0, 1)
                    .setWeights(1, 1)
                    .setMargin(0, leftMargin, bottomMarginInGroup, 0));
        add(importlabel,
            new GBC(1, 1)
                    .setWeights(1, 1)
                    .setMargin(0, leftMargin, bottomMarginInGroup, 0));
    }

    private void clickExportButton(ActionEvent e) {
        JFileChooser jFileChooser = getJFileChooser();

        int returnVal = jFileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("Hello from Burp");
                exportLabel.setText("Exported to " + file.getAbsolutePath());
            } catch (IOException e1) {
                exportLabel.setText("Export to file %s failed.".formatted(file.getAbsolutePath()));
                e1.printStackTrace();
            }
        } else {
            exportLabel.setText("");
        }
    }

    /**
     * Extend default chooser with confirmation dialog if the file already exists
     *
     * @return JFileChooser
     */
    private JFileChooser getJFileChooser() {
        JFileChooser jFileChooser = new JFileChooser() {
            @Override
            public void approveSelection() {
                File f = getSelectedFile();
                if (getDialogType() == SAVE_DIALOG && f.exists()) {
                    int result = JOptionPane.showConfirmDialog(this,
                                                               "This file already exists. Do you want to overwrite it?",
                                                               "Overwrite existing file?",
                                                               JOptionPane.YES_NO_CANCEL_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        super.approveSelection();
                    } else {
                        super.cancelSelection();
                    }
                } else {
                    super.approveSelection();
                }
            }
        };

        // Add filter to only show JSON files
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setSelectedFile(new File("collaboraider_export.json"));
        jFileChooser.setFileFilter(new FileNameExtensionFilter("json file", "json"));

        return jFileChooser;
    }

    private void clickImportButton(ActionEvent e) {
        JFileChooser jFileChooser = getJFileChooser();
        int returnVal = jFileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile();
            importlabel.setText("Imported " + file.getAbsolutePath());
        }
    }
}
