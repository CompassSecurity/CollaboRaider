package ch.csnc.pingback;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.collaborator.Interaction;
import burp.api.montoya.persistence.PersistedObject;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import ch.csnc.settings.SettingsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PingbackHandler {
    private final MontoyaApi montoyaApi;
    private final PingbackTableModel tableModel;
    private final SettingsModel settings;

    private final String KEY_PINGBACK_NUM_ROWS = "PREFERENCES_KEY_PINGBACK_NUM_ROWS";
    private final String KEY_PINGBACK_ROW = "KEY_PINGBACK_ROW_";

    public PingbackHandler(MontoyaApi montoyaApi,
                           SettingsModel settings) {
        this.montoyaApi = montoyaApi;
        this.settings = settings;


        // Initialize table model with previously stored interactions
        if (montoyaApi.persistence().extensionData().getInteger(KEY_PINGBACK_NUM_ROWS) != null) {
            int numRows = montoyaApi.persistence().extensionData().getInteger(KEY_PINGBACK_NUM_ROWS);
            List<Pingback> pingbacks = new ArrayList<>(numRows);
            for (int i = 0; i < numRows; ++i) {
                PersistedObject object = montoyaApi.persistence().extensionData().getChildObject(KEY_PINGBACK_ROW + i);
                Pingback pingback = Pingback.fromPersistence(object);
                pingbacks.add(i, pingback);
            }
            tableModel = new PingbackTableModel(pingbacks);
        } else {
            tableModel = new PingbackTableModel();
        }
    }

    public PingbackTableModel getTableModel() {
        return tableModel;
    }

    public void handleInteraction(Interaction interaction) {

        // Check if the interaction was caused by the initial request to determine the own IP address
        if (Objects.equals(interaction.id().toString(), settings.getCheckIpPayload().id().toString())) {

            montoyaApi.logging()
                      .logToOutput("Own IP (%s): %s".formatted(interaction.type().name(),
                                                               interaction.clientIp().getHostAddress()));
            // settings.ownIPAddresses.add(interaction.clientIp().getHostAddress());
            settings.getOwnIPAddresses().add(interaction.clientIp().getHostAddress());
            return;
        }

        // Search for all occurrences of the collaborator ID that caused this interaction
        List<ProxyHttpRequestResponse> proxyList = montoyaApi.proxy()
                                                             .history(requestResponse ->
                                                                              requestResponse.finalRequest()
                                                                                             .toString()
                                                                                             .contains(interaction.id()
                                                                                                                  .toString()));

        // Log to output
        montoyaApi.logging()
                  .logToOutput(String.format(
                          "Got interaction %s (%s) from IP %s. Own id is %s. Found %d corresponding responses",
                          interaction.type().name(),
                          interaction.id(),
                          interaction.clientIp(),
                          settings.getCheckIpPayload().id().toString(),
                          proxyList.size()));

        // Process each request
        for (ProxyHttpRequestResponse item : proxyList) {
            processInteractionWithProxyItem(interaction, item);
        }

    }

    private void processInteractionWithProxyItem(Interaction interaction, ProxyHttpRequestResponse item) {

        // Check if this pingback came from own IP
        boolean fromOwnIP = settings.getOwnIPAddresses().contains(interaction.clientIp().getHostAddress());
        // If setting is enabled, ignore this request
        if (fromOwnIP && settings.getActionForOwnIP() == SettingsModel.ActionForOwnIP.DROP) {
            return;
        }

        // Add to table
        Pingback pingback = new Pingback(item.finalRequest(), item.response(), item.time(), interaction, fromOwnIP);
        tableModel.add(pingback);

        // Add to persistence
        int numRows = 0;
        if (montoyaApi.persistence().extensionData().getInteger(KEY_PINGBACK_NUM_ROWS) != null) {
            numRows = montoyaApi.persistence().extensionData().getInteger(KEY_PINGBACK_NUM_ROWS);
        }
        montoyaApi.persistence().extensionData().setChildObject(KEY_PINGBACK_ROW + numRows, pingback.toPersistence());
        montoyaApi.persistence().extensionData().setInteger(KEY_PINGBACK_NUM_ROWS, ++numRows);

        montoyaApi.logging().logToOutput(" -> added to table.");
        montoyaApi.logging().logToOutput(" -> #entries: " + tableModel.getRowCount());

        // Set comment and highlight in Proxy tab (if enabled)
        if (settings.getCommentsEnabled()) {
            item.annotations()
                .setNotes("CollaboRaider: Received %s pingback for %s %s".formatted(interaction.type().name(),
                                                                                    pingback.getPayloadType(),
                                                                                    pingback.getPayloadKey()));
        }

        // Highlight in Proxy
        item.annotations().setHighlightColor(settings.getProxyHighlightColor());

        // Create audit issue
        PingbackAuditIssue issue = new PingbackAuditIssue(pingback);
        montoyaApi.siteMap().add(issue);
        montoyaApi.logging().logToOutput(" -> added issue.");

    }
}
