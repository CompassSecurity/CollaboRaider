package ch.csnc.gui.payloads;

import javax.swing.*;

public class DescriptionLabel extends JLabel {
    public DescriptionLabel() {
        setHorizontalAlignment(SwingConstants.LEFT);
        setVerticalAlignment(SwingConstants.TOP);

        // Explicitly allow HTML content
        putClientProperty("html.disable", null);
        // descriptionLabel.setContentType("text/html");

        setText("""
                <html>
                Use placeholders to define payloads:
                <ul>
                <li><b><tt>%s</tt></b> will be replaced with a randomly generated Collaborator URL.</li>
                <li><b><tt>%h</tt></b> will be replaced with the <b>host</b> of the current request.</li>
                <li><b><tt>%o</tt></b> will be replaced with the <b>origin</b> of the current request.</li>
                <li><b><tt>%r</tt></b> will be replaced with the <b>referer</b> of the current request.</li>                          
                </ul>
                </html>
                """);
    }
}
