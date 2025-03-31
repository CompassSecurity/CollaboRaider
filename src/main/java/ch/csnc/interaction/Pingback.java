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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    public String getHTMLDescription() {
        String data = "The collaborator server received a %s pingback from IP address <b>%s</b> at %s.<br>".formatted(
                getPingbackType(),
                getInteractionClientIp(),
                getLocalTimestamp());
        data += "This pingback was caused by a payload in the %s <b>%s</b>.".formatted(getPayloadType(),
                                                                                       getPayloadKey());

        // DNS details
        if (interaction.type() == InteractionType.DNS && interaction.dnsDetails().isPresent()) {

        }

        // HTTP details
        else if (interaction.type() == InteractionType.HTTP && interaction.httpDetails().isPresent()) {

        }

        // SMTP details
        else if (interaction.type() == InteractionType.SMTP && interaction.smtpDetails().isPresent()) {

        }


        return data;
    }
}
