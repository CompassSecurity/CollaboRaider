package ch.csnc.pingback;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.collaborator.Interaction;
import burp.api.montoya.collaborator.InteractionType;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import burp.api.montoya.scanner.audit.issues.AuditIssueSeverity;
import ch.csnc.Extension;
import ch.csnc.settings.SettingsModel;

import java.util.List;
import java.util.Objects;

public class PingbackHandler {
    private final MontoyaApi montoyaApi;
    private final PingbackTableModel tableModel;
    private final SettingsModel settings;

    public PingbackHandler(MontoyaApi montoyaApi,
                           SettingsModel settings) {
        this.montoyaApi = montoyaApi;
        this.settings = settings;

        // Initialize table model from persistence data
        tableModel = new PingbackTableModel(montoyaApi.persistence().extensionData());
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
            // Add to list of own addresses
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
                          "Got interaction %s (%s) from IP %s. Found %d corresponding response%s.",
                          interaction.type().name(),
                          interaction.id(),
                          interaction.clientIp().getHostAddress(),
                          proxyList.size(),
                          (proxyList.size() == 1) ? "" : "s"));

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

        // Log to output
        // montoyaApi.logging().logToOutput(" -> added to table.");
        // montoyaApi.logging().logToOutput(" -> #entries: " + tableModel.getRowCount());

        // Set comment and highlight in Proxy tab (if enabled)
        if (settings.getCommentsEnabled()) {
            item.annotations()
                .setNotes("%s: Received %s pingback for %s %s".formatted(
                        Extension.name,
                        interaction.type().name(),
                        pingback.getPayloadType(),
                        pingback.getPayloadKey()));
        }

        // Highlight in Proxy
        item.annotations().setHighlightColor(settings.getProxyHighlightColor());

        // Set severity
        AuditIssueSeverity severity;
        if (fromOwnIP)
            severity = AuditIssueSeverity.LOW;
        else if (interaction.type() == InteractionType.DNS)
            severity = settings.getPingbackSeverityDNS();
        else if (interaction.type() == InteractionType.HTTP)
            severity = settings.getPingbackSeverityHTTP();
        else if (interaction.type() == InteractionType.SMTP)
            severity = settings.getPingbackSeveritySMTP();
        else
            severity = AuditIssueSeverity.FALSE_POSITIVE;

        // Create audit issue
        PingbackAuditIssue issue = new PingbackAuditIssue(pingback, severity);
        montoyaApi.siteMap().add(issue);
        // montoyaApi.logging().logToOutput(" -> added issue.");
    }
}
