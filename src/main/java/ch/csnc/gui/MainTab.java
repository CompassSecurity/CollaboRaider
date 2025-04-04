package ch.csnc.gui;

import burp.api.montoya.MontoyaApi;
import ch.csnc.interaction.PingbackTableModel;
import ch.csnc.payload.PayloadsTableModel;
import ch.csnc.settings.SettingsModel;

import javax.swing.*;

public class MainTab extends JTabbedPane {
    public MainTab(MontoyaApi montoyaApi,
                   PingbackTableModel pingbackTableModel,
                   PayloadsTableModel settingsTableModel,
                   SettingsModel settingsModel) {
        super();

        InteractionsTab interactionsTab = new InteractionsTab(montoyaApi, pingbackTableModel, settingsModel);
        PayloadsTab payloadsTab = new PayloadsTab(montoyaApi, settingsTableModel);
        SettingsTab settingsTab = new SettingsTab(settingsModel);

        this.addTab(settingsTab.TAB_TITLE, new JScrollPane(settingsTab));
        this.addTab(interactionsTab.TAB_TITLE, interactionsTab);
        this.addTab(payloadsTab.TAB_TITLE, payloadsTab);

    }
}
