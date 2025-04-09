package ch.csnc.gui.components;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class FileChooser extends JFileChooser {
    Component parentComponent;

    public FileChooser(Component parentComponent) {
        super();
        this.parentComponent = parentComponent;
    }
    @Override
    public void approveSelection() {
        File f = getSelectedFile();
        if (getDialogType() == SAVE_DIALOG && f.exists()) {
            int result = JOptionPane.showConfirmDialog(parentComponent,
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

    public String saveFile(String data) throws IOException {
        int returnVal = showSaveDialog(parentComponent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = getSelectedFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(data);
            writer.close();
            return file.getAbsolutePath();
        } else {
            return "none";
        }
    }

    public InputStream openFile() throws FileNotFoundException {
        int returnVal = showOpenDialog(parentComponent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = getSelectedFile();
            InputStream inputStream = new FileInputStream(file);
            return inputStream;
        }
        return null;
    }
}
