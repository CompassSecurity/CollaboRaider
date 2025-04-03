package ch.csnc.gui.settings;

import ch.csnc.gui.GBC;

import javax.swing.*;

public class AboutPanel extends SettingsPanel {
    AboutPanel() {
        super("About");

        add(new JLabel("Version:"),
            new GBC(0, 0)
                    .setWeights(0, 0)
                    .setMargin(0, 0, bottomMarginInGroup, 0));
        add(new JLabel("0.1"),
            new GBC(0, 1)
                    .setWeights(0, 1)
                    .setMargin(0, leftMargin, bottomMarginInGroup, 0));

        add(new JLabel("Build date:"),
            new GBC(1, 0)
                    .setWeights(0, 0)
                    .setMargin(0, 0, bottomMarginInGroup, 0));
        add(new JLabel("2025-04-03 02:01:00"),
            new GBC(1, 1)
                    .setWeights(0, 1)
                    .setMargin(0, leftMargin, bottomMarginInGroup, 0));

        JLabel developerLabel = new JLabel();
        developerLabel.putClientProperty("html.disable", null);
        //developerLabel.setContentType("text/html");
        developerLabel.setOpaque(false);
        //developerLabel.setEditable(false);
        developerLabel.setText("""
            <html>
            Developed by Compass Security (<a href=\"http://compass-security.com\">http://compass-security.com</a>), <br>
            based on the extension <i>Collaborator Everywhere</i> (<a href=\"https://github.com/PortSwigger/collaborator-everywhere\">https://github.com/PortSwigger/collaborator-everywhere</a>) by James 'albinowax' Kettle.
            </html>
            """);
        add(developerLabel,
            new GBC(2, 0)
                    .setWeights(0, 1)
                    .setSize(1, 2));

    }
}
