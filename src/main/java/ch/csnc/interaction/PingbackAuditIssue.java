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

public class PingbackAuditIssue implements AuditIssue {
    Pingback pingback;

    PingbackAuditIssue(Pingback pingback) {
        this.pingback = pingback;
    }

    @Override
    public String name() {
        return String.format("%s: Received %s Pingback for %s %s",
                Extension.name,
                pingback.interaction.type().name(),
                pingback.payloadType.label,
                pingback.payloadKey
        );
    }

    @Override
    public String detail() {
        String issueMessage = "";
        issueMessage += "The collaborator was contacted by <b>" + pingback.interaction.clientIp().getHostAddress() + "</b>.<br>\n";
        if (pingback.fromOwnIP) {
            issueMessage += "<b>This interaction was issued by your own IP address.</b><br>\n";
        }
        issueMessage += "Canonical host name: <b>" + pingback.interaction.clientIp().getCanonicalHostName() + "</b><br>\n";
        issueMessage += "The following URL was used: <b>" + pingback.payloadValue + "</b><br><br>\n";


        if (pingback.interaction.dnsDetails().isPresent()) {
            issueMessage += "<b>DNS Details:</b><br>\n";
            issueMessage += "Query type: <b>" + pingback.interaction.dnsDetails().get().queryType().name() + "</b><br>\n";
        }
        if (pingback.interaction.httpDetails().isPresent()) {
            issueMessage += "<b>HTTP Details:</b><br>\n";
            issueMessage += "Protocol: " + pingback.interaction.httpDetails().get().protocol().name() + "\n<br>";
            issueMessage += "Request: <pre>" + pingback.interaction.httpDetails().get().requestResponse().request().toString() + "\n</pre><br>";
        }
        if (pingback.interaction.customData().isPresent()) {
            issueMessage += "<b>Custom Data:</b><br>\n";
            issueMessage += pingback.interaction.customData().get() + "<br>\n";
        }

        return issueMessage;

    }

    @Override
    public String remediation() {
        return "Avoid using user input as a source for the target of a request or prevent access to this functionality if possible. " +
                "Alternatively, only allow access to whitelisted targets.";
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
        HttpRequestResponse requestResponse = HttpRequestResponse.httpRequestResponse(pingback.request, pingback.response)
                .withRequestMarkers(requestHighlightMarker);
        return List.of(requestResponse);
    }

    @Override
    public List<Interaction> collaboratorInteractions() {
        return List.of(pingback.interaction);
    }

    @Override
    public AuditIssueDefinition definition() {
        String background =
                "The server can be tricked into performing requests to other systems. " +
                "This is known as server-side request forgery (SSRF).";

        String name = "SSRF";

        String remediation =
                "Avoid using user input as a source for the target of a request or prevent access to this functionality if possible. " +
                "Alternatively, only allow access to whitelisted targets.";

        AuditIssueSeverity typicalSeverity = AuditIssueSeverity.MEDIUM;

        return AuditIssueDefinition.auditIssueDefinition(name, background, remediation, typicalSeverity);
    }
}
