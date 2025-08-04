package ch.csnc.gui.payloads;

import ch.csnc.settings.SettingsModel;

import javax.swing.*;

public class DescriptionLabel extends JLabel {
    public DescriptionLabel(SettingsModel settingsModel) {
        setHorizontalAlignment(SwingConstants.LEFT);
        setVerticalAlignment(SwingConstants.TOP);

        // Explicitly allow HTML content
        putClientProperty("html.disable", null);

        setText("""
                <html>
                Use placeholders to define payloads:
                <ul>
                <li><b><tt>%%s</tt></b> will be replaced with a randomly generated Collaborator URL, eg. <tt>%s</tt>.</li>
                <li><b><tt>%%h</tt></b> will be replaced with the <b>Host</b> header value of the current request.</li>
                <li><b><tt>%%o</tt></b> will be replaced with the <b>Origin</b> header value of the current request.</li>
                <li><b><tt>%%r</tt></b> will be replaced with the <b>Referer</b> header value of the current request.</li>
                </ul>
                </html>
                """.formatted(settingsModel.getCheckIpPayload()));
    }
}
