package ch.csnc.gui;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.UserInterface;
import ch.csnc.gui.interactions.DetailsPane;
import ch.csnc.gui.interactions.InteractionListPane;
import ch.csnc.gui.interactions.TabController;
import ch.csnc.pingback.Pingback;
import ch.csnc.pingback.PingbackTableModel;
import ch.csnc.settings.SettingsModel;

import javax.swing.*;
import java.awt.*;

public class InteractionsTab extends JSplitPane implements TabController {
    public final String TAB_TITLE = "Interactions";
    private final MontoyaApi api;
    private final SettingsModel settingsModel;

    InteractionListPane listPane;
    DetailsPane detailsPane;
    PingbackTableModel tableModel;

    public InteractionsTab(MontoyaApi api, PingbackTableModel tableModel, SettingsModel settingsModel) {
        this.api = api;
        this.settingsModel = settingsModel;
        this.tableModel = tableModel;

        setName(TAB_TITLE);

        // Define split pane
        this.setOrientation(JSplitPane.VERTICAL_SPLIT);

        // Create pane for log entries
        listPane = new InteractionListPane(this, tableModel);
        this.setLeftComponent(listPane);

        // Create pane for details
        detailsPane = new DetailsPane(this);
        this.setRightComponent(detailsPane);

        // Set initial visibility to false until an entry is selected
        detailsPane.setVisible(false);
        detailsPane.setMinimumSize(new Dimension(0, 0));
    }

    @Override
    public UserInterface getUserInterface() {
        return api.userInterface();
    }

    @Override
    public void updateDetailsView(Pingback pingback) {
        detailsPane.updateDetails(pingback);

        // The pane is invisible until the first entry is selected
        if (!detailsPane.isVisible()) {
            detailsPane.setVisible(true);
            setDividerLocation(0.75);
        }
    }

    @Override
    public void hideDetailsView() {
        detailsPane.setVisible(false);
    }

}

