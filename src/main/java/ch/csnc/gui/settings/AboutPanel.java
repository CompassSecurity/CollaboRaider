package ch.csnc.gui.settings;

import ch.csnc.gui.GBC;
import ch.csnc.settings.SettingsModel;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class AboutPanel extends AbstractSettingsPanel {
    public AboutPanel(SettingsModel settingsModel) {
        super("About");

        String versionTooltip = "You are now a developer.";
        JLabel versionLabel = new JLabel("Version:");
        versionLabel.setToolTipText(versionTooltip);
        JLabel versionValue = new JLabel(settingsModel.getVersion());
        versionValue.setToolTipText(versionTooltip);

        String buildTooltip = "No need, you are already a developer.";
        JLabel buildLabel = new JLabel("Build date:");
        buildLabel.setToolTipText(buildTooltip);
        JLabel buildValue = new JLabel(settingsModel.getBuildTime());
        buildValue.setToolTipText(buildTooltip);

        // To create a label with clickable links, JEditorPane is used instead of a JLabel
        JEditorPane developerLabel = new JEditorPane();
        developerLabel.putClientProperty("html.disable", null);
        developerLabel.setOpaque(false);
        developerLabel.setContentType("text/html");
        developerLabel.setEditable(false);
        developerLabel.setFocusable(false);
        developerLabel.setBackground(UIManager.getColor("Label.background"));
        developerLabel.setBorder(UIManager.getBorder("Label.border"));
        developerLabel.setToolTipText("aHR0cHM6Ly9wemwuY29tcGFzcy1kZW1vLmNvbS9yaWRkbGUuaHRtbA==");

        // Add listener to open links
        developerLabel.addHyperlinkListener(hle -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                try {
                    Desktop.getDesktop().browse(hle.getURL().toURI());
                } catch (IOException | URISyntaxException e) {
                    // ignore
                }
            }
        });
        developerLabel.setText("""
                               <html>
                               Developed by Andreas Brombach @ Compass Security (<a href="https://www.compass-security.com">https://compass-security.com</a>), <br>
                               based on the extension <i>Collaborator Everywhere</i> (<a href="https://github.com/PortSwigger/collaborator-everywhere">https://github.com/PortSwigger/collaborator-everywhere</a>) by James 'albinowax' Kettle.
                               </html>
                               """);

        // Assemble layout

        // First row: Build version
        add(versionLabel,
            new GBC(0, 0)
                    .setWeights(0, 0)
                    .setMargin(0, 0, bottomMarginInGroup, 0));

        add(versionValue,
            new GBC(0, 1)
                    .setWeights(0, 1)
                    .setMargin(0, leftMargin, bottomMarginInGroup, 0));


        // Second row: Build version
        add(buildLabel,
            new GBC(1, 0)
                    .setWeights(0, 0)
                    .setMargin(0, 0, bottomMarginInGroup, 0));

        add(buildValue,
            new GBC(1, 1)
                    .setWeights(0, 1)
                    .setMargin(0, leftMargin, bottomMarginInGroup, 0));


        // Third row: developer infos
        add(developerLabel,
            new GBC(2, 0)
                    .setWeights(0, 1)
                    .setSize(1, 2));

    }
}
