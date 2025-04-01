package ch.csnc.interaction;

import burp.api.montoya.collaborator.Interaction;
import burp.api.montoya.collaborator.InteractionType;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.params.ParsedHttpParameter;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import ch.csnc.payload.PayloadType;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Pingback {
    public HttpRequest request;
    public HttpResponse response;
    public Interaction interaction;
    public boolean fromOwnIP;
    public String payloadKey, payloadValue;
    ZonedDateTime requestTime;
    PayloadType payloadType;


    public Pingback(ProxyHttpRequestResponse item, Interaction interaction, boolean fromOwnIP) {
        this.request = item.finalRequest();
        this.response = item.response();
        this.requestTime = item.time();
        this.interaction = interaction;
        this.fromOwnIP = fromOwnIP;

        // Try to find header which contains the ID
        List<HttpHeader> headers = request.headers();
        for (HttpHeader header : headers) {
            if (header.value().contains(interaction.id().toString())) {
                payloadType = PayloadType.HEADER;
                payloadKey = header.name();
                payloadValue = header.value();
                break;
            }
        }

        // Check URL parameter if no header was found
        if (payloadKey == null) {
            List<ParsedHttpParameter> urlParams = request.parameters(HttpParameterType.URL);
            for (ParsedHttpParameter urlParam : urlParams) {
                if (urlParam.value().contains(interaction.id().toString())) {
                    payloadType = PayloadType.PARAM;
                    payloadKey = urlParam.name();
                    payloadValue = urlParam.value();
                    break;
                }
            }
        }
    }

    public String getLocalTimestamp() {
        // really? is there a better way to do this?
        return interaction.timeStamp()
                          .withZoneSameInstant(ZoneId.systemDefault())
                          .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    public String getPingbackType() {
        return interaction.type().toString();
    }

    public String getInteractionId() {
        return interaction.id().toString();
    }

    public String getInteractionClientIp() {
        return interaction.clientIp().getHostAddress();
    }

    public String getPayloadType() {
        return payloadType.label;
    }

    public String getPayloadKey() {
        return payloadKey;
    }

    public String getDescription() {
        String data = "The collaborator server received a %s pingback from IP address <b>%s</b> at %s.<br>".formatted(
                getPingbackType(),
                getInteractionClientIp(),
                getLocalTimestamp());

        // Perform reverse DNS lookup of the IP address
        String canonicalHostName = interaction.clientIp().getCanonicalHostName();
        if (!Objects.equals(canonicalHostName, getInteractionClientIp()))
            data += "Canonical host name: <b>%s</b><br>"
                    .formatted(canonicalHostName);

        // Show hint if the request originated from the same IP address that was determined at startup
        if (fromOwnIP) {
            data += "<b style=\"color:blue;\">Info: This interaction was received from your own IP address.</b><br>";
        }

        data += "This pingback was caused by a payload in the %s <b>%s</b>.<br>".formatted(getPayloadType(),
                                                                                           getPayloadKey());
        // Calculate duration
        Duration duration = Duration.between(requestTime, interaction.timeStamp());
        String durationString = duration.toString()
                                        .substring(2)
                                        .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                                        .toLowerCase();
        data += "It was received <b>%s</b> after the request containing the payload was sent.<br><br>"
                .formatted(durationString);

        // Show details for DNS pingback
        if (interaction.type() == InteractionType.DNS && interaction.dnsDetails().isPresent()) {
            data += "<b>DNS Details:</b><br>";
            data += "Query type: <b>%s</b><br>"
                    .formatted(interaction.dnsDetails().get().queryType().name());
            // TODO: Parse raw DNS query to get more info
            data += "Raw query: <br><pre>%s</pre><br>".formatted(interaction.dnsDetails().get().query().toString());
        }

        // HTTP details
        if (interaction.type() == InteractionType.HTTP && interaction.httpDetails().isPresent()) {
            data += "<b>HTTP Details:</b><br>";
            data += "Protocol: %s<br>"
                    .formatted(interaction.httpDetails().get().protocol().name());
            data += "Request: <pre>%s</pre><br>"
                    .formatted(interaction.httpDetails().get().requestResponse().request().toString());
        }

        // SMTP details
        if (interaction.type() == InteractionType.SMTP && interaction.smtpDetails().isPresent()) {
            data += "<b>SMTP Details:</b><br>";
            data += "Protocol: %s<br>"
                    .formatted(interaction.smtpDetails().get().protocol().name());
            data += "SMTP Conversation:<br><pre>%s</pre><br>"
                    .formatted(interaction.smtpDetails().get().conversation());
        }

        // Show custom data (if it exists)
        if (interaction.customData().isPresent()) {
            data += "<b>Custom Data:</b><br>";
            data += "%s<br>"
                    .formatted(interaction.customData().get());
        }

        return data;
    }
}
