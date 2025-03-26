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
        issueMessage += "The collaborator was contacted by <b>%s</b>.<br>"
                .formatted(hostAddress);

        if (pingback.fromOwnIP) {
            issueMessage += "<b>This interaction was issued by your own IP address.</b><br>";
        }

        String canonicalHostName = pingback.interaction.clientIp().getCanonicalHostName();
        if (!Objects.equals(canonicalHostName, hostAddress))
            issueMessage += "Canonical host name: <b>%s</b><br>"
                    .formatted(canonicalHostName);

        issueMessage += "The following URL was used: <b>%s</b><br>"
                .formatted(pingback.payloadValue);

        Duration duration = Duration.between(pingback.requestTime, pingback.interaction.timeStamp());
        String durationString = duration.toString()
                                        .substring(2)
                                        .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                                        .toLowerCase();
        issueMessage += "The pingback was received <b>%s</b> after the request.<br><br>"
                .formatted(durationString);

        // Show details for DNS pingback
        if (pingback.interaction.dnsDetails().isPresent()) {
            issueMessage += "<b>DNS Details:</b><br>";
            issueMessage += "Query type: <b>%s</b><br>"
                    .formatted(pingback.interaction.dnsDetails().get().queryType().name());
            // TODO: Parse raw DNS query to get more info
            // issueMessage += "Raw query: <br><pre>%s</pre><br>".formatted(pingback.interaction.dnsDetails().get().query().toString());
        }

        // Show details for HTTP(S) pingback
        if (pingback.interaction.httpDetails().isPresent()) {
            issueMessage += "<b>HTTP Details:</b><br>";
            issueMessage += "Protocol: %s<br>"
                    .formatted(pingback.interaction.httpDetails().get().protocol().name());
            issueMessage += "Request: <pre>%s</pre><br>"
                    .formatted(pingback.interaction.httpDetails().get().requestResponse().request().toString());
        }

        // Show details for SMTP(S) pingback
        if (pingback.interaction.smtpDetails().isPresent()) {
            issueMessage += "<b>SMTP Details:</b><br>";
            issueMessage += "Protocol: %s<br>"
                    .formatted(pingback.interaction.smtpDetails().get().protocol().name());
            issueMessage += "SMTP Conversation:<br><pre>%s</pre>"
                    .formatted(pingback.interaction.smtpDetails().get().conversation());
        }

        // Show custom data
        if (pingback.interaction.customData().isPresent()) {
            issueMessage += "<b>Custom Data:</b><br>";
            issueMessage += "%s<br>"
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
