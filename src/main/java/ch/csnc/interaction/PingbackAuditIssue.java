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

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class PingbackAuditIssue implements AuditIssue {
    Pingback pingback;

    PingbackAuditIssue(Pingback pingback) {
        this.pingback = pingback;
    }

    @Override
    public String name() {
        return "%s: %s Pingback for %s %s"
                .formatted(
                        Extension.name,
                        pingback.interaction.type().name(),
                        pingback.payloadType.label,
                        pingback.payloadKey
                );
    }

    @Override
    public String detail() {
        return pingback.getDescription();
    }

    @Override
    public String remediation() {
        return "Avoid using user input as a source for the target of a request or prevent access to this functionality if possible. Alternatively, only allow access to whitelisted targets.";
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
        if (pingback.fromOwnIP)
            return AuditIssueSeverity.MEDIUM;
        else
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
        HttpRequestResponse requestResponse = HttpRequestResponse.httpRequestResponse(pingback.request,
                                                                                      pingback.response)
                                                                 .withRequestMarkers(requestHighlightMarker);
        return List.of(requestResponse);
    }

    @Override
    public List<Interaction> collaboratorInteractions() {
        return List.of(pingback.interaction);
    }

    @Override
    public AuditIssueDefinition definition() {
        String name = "SSRF";

        String background = "The server might be tricked into performing requests to other systems. " +
                "This is known as server-side request forgery (SSRF).";

        String remediation = "Avoid using user input as a source for the target of a request or prevent access to this functionality if possible. " +
                "Alternatively, only allow access to whitelisted targets.";

        AuditIssueSeverity typicalSeverity = AuditIssueSeverity.MEDIUM;

        return AuditIssueDefinition.auditIssueDefinition(name, background, remediation, typicalSeverity);
    }
}
