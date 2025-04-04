package ch.csnc.interaction;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.collaborator.CollaboratorPayload;
import burp.api.montoya.collaborator.Interaction;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import ch.csnc.settings.SettingsModel;

import java.util.List;
import java.util.Objects;

public class PingbackHandler {
    private final MontoyaApi montoyaApi;
    private final PingbackTableModel tableModel;
    private final SettingsModel settings;

    public PingbackHandler(MontoyaApi montoyaApi,
                           PingbackTableModel tableModel,
                           SettingsModel settings) {
        this.montoyaApi = montoyaApi;
        this.tableModel = tableModel;
        this.settings = settings;

        // Initialize list to hold own IP addresses
        // settings.ownIPAddresses.init();
        //settings.ownIPAddresses = new ArrayList<>();
    }

    public void handleInteraction(Interaction interaction) {

        // Check if the interaction was caused by the initial request to determine the own IP address
        if (Objects.equals(interaction.id().toString(), settings.getCheckIpPayload().id().toString())) {

            montoyaApi.logging()
                      .logToOutput("Own IP (%s): %s".formatted(interaction.type().name(),
                                                               interaction.clientIp().getHostAddress()));
            // settings.ownIPAddresses.add(interaction.clientIp().getHostAddress());
            settings.ownIPAddresses.add(interaction.clientIp().getHostAddress());
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
                  .logToOutput(String.format("Got interaction %s (%s) from IP %s. Own id is %s. Found %d corresponding responses",
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
        //String fullCollaboratorURL = interaction.id().toString() + "." + collaboratorServerAddress;

        // Check if this pingback came from the own IP
        //boolean fromOwnIP = settings.ownIPAddresses.contains(interaction.clientIp().getHostAddress());
        boolean fromOwnIP = settings.ownIPAddresses.contains(interaction.clientIp().getHostAddress());
        // If setting is enabled, ignore this request
        if (fromOwnIP && settings.getActionForOwnIP() == SettingsModel.ActionForOwnIP.DROP) {
            return;
        }

        // Add to table
        Pingback pingback = new Pingback(item, interaction, fromOwnIP);
        tableModel.add(pingback);
        montoyaApi.logging().logToOutput(" -> added to table.");
        montoyaApi.logging().logToOutput(" -> #entries: " + tableModel.getRowCount());

        // Set comment and highlight in Proxy tab (if enabled)
        if (settings.getCommentsEnabled()) {
            item.annotations()
                .setNotes("CollaboRaider: Received %s pingback for %s %s".formatted(interaction.type().name(),
                                                                                    pingback.getPayloadType(),
                                                                                    pingback.getPayloadKey()));
        }

        item.annotations().setHighlightColor(settings.getProxyHighlightColor());

        // Create audit issue
        PingbackAuditIssue issue = new PingbackAuditIssue(pingback);
        montoyaApi.siteMap().add(issue);
        montoyaApi.logging().logToOutput(" -> added issue.");

    }
}
