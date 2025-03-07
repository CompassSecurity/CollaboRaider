package ch.csnc.interaction;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.collaborator.CollaboratorPayload;
import burp.api.montoya.collaborator.Interaction;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import ch.csnc.settings.SettingsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PingbackHandler {
    private final MontoyaApi montoyaApi;
    private final PingbackTableModel tableModel;
    private final String collaboratorServerAddress;
    private final CollaboratorPayload checkIpPayload;
    private final List<String> ownIPAddresses;

    public PingbackHandler(MontoyaApi montoyaApi, PingbackTableModel tableModel, String collaboratorServerAddress, CollaboratorPayload checkIpPayload) {
        this.montoyaApi = montoyaApi;
        this.tableModel = tableModel;
        this.collaboratorServerAddress = collaboratorServerAddress;
        this.checkIpPayload = checkIpPayload;

        // Initialize list to hold own IP addresses
        ownIPAddresses = new ArrayList<>();
    }

    public void handleInteraction(Interaction interaction) {

        // Check if the interaction was caused by the initial request to determine the own IP address
        if (Objects.equals(interaction.id().toString(), checkIpPayload.id().toString())) {
            montoyaApi.logging().logToOutput("Own IP (" + interaction.type().name() + "): " + interaction.clientIp().getHostAddress());
            ownIPAddresses.add(interaction.clientIp().getHostAddress());
            return;
        }

        // Search for all occurrences of the collaborator ID that caused this interaction
        List<ProxyHttpRequestResponse> proxyHttpRequestResponseList = montoyaApi.proxy().history(requestResponse -> requestResponse.finalRequest().toString().contains(interaction.id().toString()));

        // Log to output
        montoyaApi.logging().logToOutput(String.format("Got interaction %s (%s) from IP %s. Found %d corresponding responses", interaction.type().name(), interaction.id(), interaction.clientIp(), proxyHttpRequestResponseList.size()));

        // Process each request
        for (ProxyHttpRequestResponse item : proxyHttpRequestResponseList) {
            processInteractionWithProxyItem(interaction, item);
        }

    }

    private void processInteractionWithProxyItem(Interaction interaction, ProxyHttpRequestResponse item) {
        String fullCollaboratorURL = interaction.id().toString() + "." + collaboratorServerAddress;

        // Check if this pingback came from the own IP
        boolean fromOwnIP = ownIPAddresses.contains(interaction.clientIp().getHostAddress());
        // Ignore
        if (fromOwnIP && SettingsModel.getInstance().actionForOwnIP == SettingsModel.ActionForOwnIP.DROP) {
            return;
        }

        // Set comment and highlight in Proxy tab (if enabled)
        item.annotations().setNotes("CollaboRaider: Received " + interaction.type().name() + " pingback");
        item.annotations().setHighlightColor(SettingsModel.getInstance().proxyHighlightColor);

        // Add to table
        Pingback pingback = new Pingback(item, interaction, fromOwnIP);
        tableModel.add(pingback);
        montoyaApi.logging().logToOutput(" -> added to table.");
        montoyaApi.logging().logToOutput(" -> #entries: " + tableModel.getRowCount());

        // Create audit issue
        PingbackAuditIssue issue = new PingbackAuditIssue(pingback);
        montoyaApi.siteMap().add(issue);
        montoyaApi.logging().logToOutput(" -> added issue.");

    }
}
