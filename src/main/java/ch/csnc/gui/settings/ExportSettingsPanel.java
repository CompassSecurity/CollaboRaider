package ch.csnc.gui.settings;

import ch.csnc.gui.GBC;
import ch.csnc.gui.components.FileChooser;
import ch.csnc.settings.SettingsModel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.*;

public class ExportSettingsPanel extends AbstractSettingsPanel {
    JLabel exportLabel, importlabel;
    FileChooser fileChooser;
    SettingsModel settingsModel;

    public ExportSettingsPanel(SettingsModel settingsModel) {
        super("Import / Export Settings");

        this.settingsModel = settingsModel;

        // Create buttons
        JButton importButton = new JButton("Import settings from file");
        JButton exportButton = new JButton("Export current settings to file");

        // Initialize file chooser
        fileChooser = new FileChooser(this);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("collaborator_export.json"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("json file", "json"));

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
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                String settings = settingsModel.serialize();
                writer.write(settings);
                exportLabel.setText("Exported to " + file.getAbsolutePath());
            } catch (IOException e1) {
                exportLabel.setText("Export to file %s failed.".formatted(file.getAbsolutePath()));
                e1.printStackTrace();
            }
        } else {
            exportLabel.setText("");
        }
    }

    private void clickImportButton(ActionEvent e) {
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line = reader.readLine();

                while (line != null) {
                    stringBuilder.append(line);
                    stringBuilder.append(System.lineSeparator());
                    line = reader.readLine();
                }

                settingsModel.fromJson(stringBuilder.toString());
                importlabel.setText("Imported from file %s".formatted(file.getAbsolutePath()));
            }  catch (IOException ex) {
                ex.printStackTrace();
                importlabel.setText("Import from file %s failed.".formatted(file.getAbsolutePath()));
            }
        } else {
            importlabel.setText("");
        }
    }
}
