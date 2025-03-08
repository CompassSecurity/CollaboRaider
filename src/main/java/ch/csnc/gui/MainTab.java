package ch.csnc.gui;

import burp.api.montoya.MontoyaApi;
import ch.csnc.gui.interactions.InteractionsTab;
import ch.csnc.interaction.PingbackTableModel;
import ch.csnc.payload.PayloadsTableModel;

import javax.swing.*;

public class MainTab extends JTabbedPane {
    public MainTab(MontoyaApi montoyaApi, PingbackTableModel pingbackTableModel, PayloadsTableModel settingsTableModel) {
        super();
        InteractionsTab interactionsTab = new InteractionsTab(montoyaApi, pingbackTableModel);
        PayloadsTab payloadsTab = new PayloadsTab(montoyaApi, settingsTableModel);
        SettingsTab settingsTab = new SettingsTab();

        this.addTab(interactionsTab.TAB_TITLE, interactionsTab);
        this.addTab(payloadsTab.TAB_TITLE, payloadsTab);
        this.addTab(settingsTab.TAB_TITLE, settingsTab);

        /*
        addChangeListener(e -> {
            montoyaApi.logging().logToOutput("Current tab: " + this.getSelectedComponent().getClass().getSimpleName());
            if (this.getSelectedComponent() instanceof InteractionsTab) {
                interactionsTab.refresh();
            }
        });
        */
    }
}
