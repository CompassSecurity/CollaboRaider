package ch.csnc.interaction;

import burp.api.montoya.collaborator.Interaction;
import burp.api.montoya.core.Marker;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.scanner.audit.issues.AuditIssue;
import burp.api.montoya.scanner.audit.issues.AuditIssueConfidence;
import burp.api.montoya.scanner.audit.issues.AuditIssueDefinition;
import burp.api.montoya.scanner.audit.issues.AuditIssueSeverity;
import ch.csnc.Extension;

import java.util.List;
import java.util.Objects;

public class PingbackAuditIssue implements AuditIssue {
    Pingback pingback;

    PingbackAuditIssue(Pingback pingback) {
        this.pingback = pingback;
    }

    @Override
    public String name() {
        return "%s: Received %s Pingback for %s %s"
                .formatted(
                        Extension.name,
                        pingback.interaction.type().name(),
                        pingback.payloadType.label,
                        pingback.payloadKey
                );
    }

    @Override
    public String detail() {
        String hostAddress = pingback.interaction.clientIp().getHostAddress();

        String issueMessage = "";
        issueMessage += "The collaborator was contacted by <b>%s</b>.<br>\n"
                .formatted(hostAddress);

        if (pingback.fromOwnIP) {
            issueMessage += "<b>This interaction was issued by your own IP address.</b><br>\n";
        }

        String canonicalHostName = pingback.interaction.clientIp().getCanonicalHostName();
        if (!Objects.equals(canonicalHostName, hostAddress))
            issueMessage += "Canonical host name: <b>%s</b><br>\n"
                    .formatted(canonicalHostName);

        issueMessage += "The following URL was used: <b>%s</b><br><br>\n"
                .formatted(pingback.payloadValue);


        if (pingback.interaction.dnsDetails().isPresent()) {
            issueMessage += "<b>DNS Details:</b><br>\n";
            issueMessage += "Query type: <b>%s</b><br>\n"
                    .formatted(pingback.interaction.dnsDetails().get().queryType().name());
        }
        if (pingback.interaction.httpDetails().isPresent()) {
            issueMessage += "<b>HTTP Details:</b><br>\n";
            issueMessage += "Protocol: %s\n<br>"
                    .formatted(pingback.interaction.httpDetails().get().protocol().name());
            issueMessage += "Request: <pre>%s</pre><br>"
                    .formatted(pingback.interaction.httpDetails().get().requestResponse().request().toString());
        }
        if (pingback.interaction.customData().isPresent()) {
            issueMessage += "<b>Custom Data:</b><br>\n";
            issueMessage += "%s<br>\n"
                    .formatted(pingback.interaction.customData().get());
        }

        return issueMessage;

    }

    @Override
    public String remediation() {
        return "Avoid using user input as a source for the target of a request or prevent access to this functionality if possible. " + "Alternatively, only allow access to whitelisted targets.";
    }

    @Override
    public HttpService httpService() {
        return pingback.request.httpService();
    }

    @Override
    public String baseUrl() {
        return pingback.request.url();
    }

    @Override
    public AuditIssueSeverity severity() {
        return AuditIssueSeverity.HIGH;
    }

    @Override
    public AuditIssueConfidence confidence() {
        return AuditIssueConfidence.CERTAIN;
    }

    @Override
    public List<HttpRequestResponse> requestResponses() {
        // Annotate request with the Collaborator URL that caused the interaction
        int start = pingback.request.toString().toLowerCase().indexOf(pingback.payloadValue.toLowerCase());
        int end = start + pingback.payloadValue.length();
        Marker requestHighlightMarker = Marker.marker(start, end);
        HttpRequestResponse requestResponse = HttpRequestResponse.httpRequestResponse(pingback.request, pingback.response).withRequestMarkers(requestHighlightMarker);
        return List.of(requestResponse);
    }

    @Override
    public List<Interaction> collaboratorInteractions() {
        return List.of(pingback.interaction);
    }

    @Override
    public AuditIssueDefinition definition() {
        String background = "The server can be tricked into performing requests to other systems. " + "This is known as server-side request forgery (SSRF).";

        String name = "SSRF";

        String remediation = "Avoid using user input as a source for the target of a request or prevent access to this functionality if possible. " + "Alternatively, only allow access to whitelisted targets.";

        AuditIssueSeverity typicalSeverity = AuditIssueSeverity.MEDIUM;

        return AuditIssueDefinition.auditIssueDefinition(name, background, remediation, typicalSeverity);
    }
}
