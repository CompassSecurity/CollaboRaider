package ch.csnc.gui.interactions;

import javax.swing.*;
import java.awt.*;

public class DescriptionPanel extends JPanel {
    JTextPane textPane;

    public DescriptionPanel() {
        textPane = new JTextPane();
        // Explicitly allow HTML content
        textPane.putClientProperty("html.disable", null);
        textPane.setContentType("text/html");
        textPane.setText("<html><p>Placeholder.</p></html>");
        textPane.setMargin(new Insets(10,10,10,10));
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
