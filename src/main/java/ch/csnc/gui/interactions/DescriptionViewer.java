package ch.csnc.gui.interactions;

import javax.swing.*;
import java.awt.*;

public class DescriptionViewer extends JPanel {
    JTextPane textPane;
    public DescriptionViewer() {
        String htmlText = "<html><h1>Hello, World!</h1><p>This is a <b>JTextPane</b> with <i>HTML</i> content.</p></html>";
        textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(htmlText);

        // Explicitly allow HTML content
        textPane.putClientProperty("html.disable", null);
        setLayout(new FlowLayout(FlowLayout.LEFT));

        setOpaque(true);

        add(textPane);
    }
    public void setText(String description) {
        textPane.setText(description);
    }
}
