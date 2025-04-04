package ch.csnc.gui.components;

import javax.swing.*;
import java.awt.*;
import java.io.File;

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
}
