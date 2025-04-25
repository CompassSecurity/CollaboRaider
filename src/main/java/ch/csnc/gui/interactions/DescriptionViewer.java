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

        textPane.setMargin(new Insets(10,10,10,10));

        // Explicitly allow HTML content
        textPane.putClientProperty("html.disable", null);
        setOpaque(false);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        add(textPane, gbc);
    }

    public void setText(String description) {
        textPane.setText(description);
    }
}
