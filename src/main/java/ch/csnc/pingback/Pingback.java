package ch.csnc.pingback;

import burp.api.montoya.collaborator.*;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.HttpProtocol;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.params.ParsedHttpParameter;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.persistence.PersistedObject;
import ch.csnc.Utils;
import ch.csnc.payload.PayloadType;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Pingback {
    private final static String PERSISTENCE_KEY_REQUEST = "PINGBACK_ORIGINAL_REQUEST";
    private final static String PERSISTENCE_KEY_RESPONSE = "PINGBACK_ORIGINAL_RESPONSE";
    private final static String PERSISTENCE_KEY_TIMESTAMP = "PINGBACK_REQUEST_TIMESTAMP";
    private final static String PERSISTENCE_KEY_OWN_IP = "PINGBACK_OWN_IP";
    private final static String PERSISTENCE_KEY_INTERACTION_ID = "PINGBACK_INTERACTION_ID";
    private final static String PERSISTENCE_KEY_INTERACTION_TYPE = "PINGBACK_INTERACTION_TYPE";
    private final static String PERSISTENCE_KEY_INTERACTION_TIMESTAMP = "PINGBACK_INTERACTION_TIMESTAMP";
    private final static String PERSISTENCE_KEY_INTERACTION_IP = "PINGBACK_INTERACTION_IP";
    private final static String PERSISTENCE_KEY_INTERACTION_PORT = "PINGBACK_INTERACTION_PORT";
    private final static String PERSISTENCE_KEY_INTERACTION_DNS_TYPE = "PINGBACK_INTERACTION_DNS_TYPE";
    private final static String PERSISTENCE_KEY_INTERACTION_DNS_RAW = "PINGBACK_INTERACTION_DNS_RAW";
    private final static String PERSISTENCE_KEY_INTERACTION_HTTP_PROTOCOL = "PINGBACK_INTERACTION_HTTP_TYPE";
    private final static String PERSISTENCE_KEY_INTERACTION_HTTP_REQRSP = "PINGBACK_INTERACTION_HTTP_TYPE";
    private final static String PERSISTENCE_KEY_INTERACTION_SMTP_PROTOCOL = "PINGBACK_INTERACTION_SMTP_TYPE";
    private final static String PERSISTENCE_KEY_INTERACTION_SMTP_CONVERSATION = "PINGBACK_INTERACTION_SMTP_CONVERSATION";
    private final static String PERSISTENCE_KEY_INTERACTION_CUSTOM_DATA = "PINGBACK_INTERACTION_CUSTOM_DATA";

    public HttpRequest request;
    public HttpResponse response;
    public Interaction interaction;
    public boolean fromOwnIP;
    public String payloadKey, payloadValue;
    ZonedDateTime requestTime;
    PayloadType payloadType;

    public Pingback(HttpRequest request,
                    HttpResponse response,
                    ZonedDateTime requestTime,
                    Interaction interaction,
                    boolean fromOwnIP) {
        this.request = request;
        this.response = response;
        this.requestTime = requestTime;
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

    /**
     * Reassemble the Pingback object back from a PersistedObject by using the defined keys.
     *
     * @param persistedObject Object stored in Burp's project persistence
     * @return Pingback object
     */
    public static Pingback fromPersistence(PersistedObject persistedObject) {
        HttpRequest request = persistedObject.getHttpRequest(PERSISTENCE_KEY_REQUEST);
        HttpResponse response = persistedObject.getHttpResponse(PERSISTENCE_KEY_RESPONSE);
        ZonedDateTime timestamp = ZonedDateTime.parse(persistedObject.getString(PERSISTENCE_KEY_TIMESTAMP));
        Boolean fromOwnIp = persistedObject.getBoolean(PERSISTENCE_KEY_OWN_IP);

        Interaction interaction = getInteraction(persistedObject);

        return new Pingback(request, response, timestamp, interaction, fromOwnIp);
    }

    /**
     * Reassemble the recorded interaction back from a PersistedObject
     *
     * @param persistedObject Object stored in Burp's project persistence
     * @return Interaction as recorded by Burp Collaborator
     */
    private static Interaction getInteraction(PersistedObject persistedObject) {
        // Since the Interaction class is abstract and does not provide any public constructors,
        // we'll just implement all methods with the appropriate values
        return new Interaction() {
            @Override
            public InteractionId id() {
                return new InteractionId() {
                    @Override
                    public String toString() {
                        return persistedObject.getString(PERSISTENCE_KEY_INTERACTION_ID);
                    }
                };
            }

            @Override
            public InteractionType type() {
                return InteractionType.valueOf(persistedObject.getString(PERSISTENCE_KEY_INTERACTION_TYPE));
            }

            @Override
            public ZonedDateTime timeStamp() {
                return ZonedDateTime.parse(persistedObject.getString(PERSISTENCE_KEY_INTERACTION_TIMESTAMP));
            }

            @Override
            public InetAddress clientIp() {
                try {
                    return InetAddress.getByAddress(persistedObject.getByteArray(PERSISTENCE_KEY_INTERACTION_IP)
                                                                   .getBytes());
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public int clientPort() {
                return persistedObject.getInteger(PERSISTENCE_KEY_INTERACTION_PORT);
            }

            @Override
            public Optional<DnsDetails> dnsDetails() {
                if (type() == InteractionType.DNS) {
                    return Optional.of(new DnsDetails() {
                        @Override
                        public DnsQueryType queryType() {
                            return DnsQueryType.valueOf(persistedObject.getString(PERSISTENCE_KEY_INTERACTION_DNS_TYPE));
                        }

                        @Override
                        public ByteArray query() {
                            return persistedObject.getByteArray(PERSISTENCE_KEY_INTERACTION_DNS_RAW);
                        }
                    });
                } else {
                    return Optional.empty();
                }
            }

            @Override
            public Optional<HttpDetails> httpDetails() {
                if (type() == InteractionType.HTTP) {
                    return Optional.of(new HttpDetails() {
                        @Override
                        public HttpProtocol protocol() {
                            return HttpProtocol.valueOf(persistedObject.getString(
                                    PERSISTENCE_KEY_INTERACTION_HTTP_PROTOCOL));
                        }

                        @Override
                        public HttpRequestResponse requestResponse() {
                            return persistedObject.getHttpRequestResponse(PERSISTENCE_KEY_INTERACTION_HTTP_REQRSP);
                        }
                    });
                } else {
                    return Optional.empty();
                }
            }

            @Override
            public Optional<SmtpDetails> smtpDetails() {
                if (type() == InteractionType.SMTP) {
                    return Optional.of(new SmtpDetails() {
                        @Override
                        public SmtpProtocol protocol() {
                            return SmtpProtocol.valueOf(persistedObject.getString(
                                    PERSISTENCE_KEY_INTERACTION_SMTP_PROTOCOL));
                        }

                        @Override
                        public String conversation() {
                            return persistedObject.getString(PERSISTENCE_KEY_INTERACTION_SMTP_CONVERSATION);
                        }
                    });
                } else {
                    return Optional.empty();
                }
            }

            @Override
            public Optional<String> customData() {
                if (persistedObject.getString(PERSISTENCE_KEY_INTERACTION_CUSTOM_DATA) != null)
                    return Optional.of(persistedObject.getString(PERSISTENCE_KEY_INTERACTION_CUSTOM_DATA));
                else {
                    return Optional.empty();
                }
            }
        };
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

    /**
     * Provide a detailed description of the pingback both for the Description tab in the UI
     * and for the issue details.
     *
     * @return Description with HTML formatting
     */
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
                                        // from docs: "The format of the returned string will be PTnHnMnS,
                                        // where n is the relevant hours, minutes or seconds part of the duration"
                                        // => remove trailing "PT"
                                        .substring(2)
                                        // Add spaces to every nH, nM, nS except the last part
                                        .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                                        // don't shout so loud
                                        .toLowerCase();
        data += "It was received <b>%s</b> after the request with the payload was sent.<br><br>"
                .formatted(durationString);

        // Show details for DNS pingback
        if (interaction.type() == InteractionType.DNS && interaction.dnsDetails().isPresent()) {
            data += "<b>DNS Pingback Details:</b><br>";
            data += "Query type: <b>%s</b><br>"
                    .formatted(interaction.dnsDetails().get().queryType().name());
            // TODO: Parse raw DNS query to get more info
            String rawRequest = Utils.sanitize(interaction.dnsDetails().get().query().toString());
            data += "Raw query data: <pre>%s</pre><br>".formatted(rawRequest);
        }

        // HTTP details
        if (interaction.type() == InteractionType.HTTP && interaction.httpDetails().isPresent()) {
            data += "<b>HTTP Pingback Details:</b><br>";
            data += "Protocol: %s<br>"
                    .formatted(interaction.httpDetails().get().protocol().name());
            String rawRequest = Utils.sanitize(interaction.httpDetails().get().requestResponse().request().toString());
            data += "Request: <pre>%s</pre>"
                    .formatted(rawRequest);
        }

        // SMTP details
        if (interaction.type() == InteractionType.SMTP && interaction.smtpDetails().isPresent()) {
            data += "<b>SMTP Pingback Details:</b><br>";
            data += "Protocol: %s<br>"
                    .formatted(interaction.smtpDetails().get().protocol().name());
            // TODO: Parse SMTP conversation to get more info
            String rawRequest = Utils.sanitize(interaction.smtpDetails().get().conversation());
            data += "SMTP Conversation: <pre>%s</pre><br>"
                    .formatted(rawRequest);
        }

        // Show custom data (if it exists)
        if (interaction.customData().isPresent()) {
            data += "<b>Custom Data:</b><br>";
            data += "<pre>%s</pre>"
                    .formatted(interaction.customData().get());
        }

        return data;
    }

    /**
     * Put all data into a PersistedObject so that it can be stored in the current project.
     * This preserves all recorded data if the extension is closed and re-opened.
     *
     * @return A PersistedObject that contains all data accessible by static keys.
     */
    public PersistedObject toPersistence() {
        PersistedObject persistedObject = PersistedObject.persistedObject();
        persistedObject.setHttpRequest(PERSISTENCE_KEY_REQUEST, request);
        persistedObject.setHttpResponse(PERSISTENCE_KEY_RESPONSE, response);
        persistedObject.setString(PERSISTENCE_KEY_TIMESTAMP, requestTime.toString());
        persistedObject.setBoolean(PERSISTENCE_KEY_OWN_IP, fromOwnIP);

        // Store interaction
        persistedObject.setString(PERSISTENCE_KEY_INTERACTION_ID, interaction.id().toString());
        persistedObject.setString(PERSISTENCE_KEY_INTERACTION_TYPE, interaction.type().name());
        persistedObject.setString(PERSISTENCE_KEY_INTERACTION_TIMESTAMP, interaction.timeStamp().toString());
        persistedObject.setByteArray(PERSISTENCE_KEY_INTERACTION_IP,
                                     ByteArray.byteArray(interaction.clientIp().getAddress()));
        persistedObject.setInteger(PERSISTENCE_KEY_INTERACTION_PORT, interaction.clientPort());

        // DNS
        if (interaction.type() == InteractionType.DNS && interaction.dnsDetails().isPresent()) {
            DnsDetails details = interaction.dnsDetails().get();
            persistedObject.setString(PERSISTENCE_KEY_INTERACTION_DNS_TYPE, details.queryType().name());
            persistedObject.setByteArray(PERSISTENCE_KEY_INTERACTION_DNS_RAW, details.query());
        }

        // HTTP
        else if (interaction.type() == InteractionType.HTTP && interaction.httpDetails().isPresent()) {
            HttpDetails details = interaction.httpDetails().get();
            persistedObject.setString(PERSISTENCE_KEY_INTERACTION_HTTP_PROTOCOL, details.protocol().name());
            persistedObject.setHttpRequestResponse(PERSISTENCE_KEY_INTERACTION_HTTP_REQRSP, details.requestResponse());
        }

        // SMTP
        else if (interaction.type() == InteractionType.SMTP && interaction.smtpDetails().isPresent()) {
            SmtpDetails details = interaction.smtpDetails().get();
            persistedObject.setString(PERSISTENCE_KEY_INTERACTION_SMTP_PROTOCOL, details.protocol().name());
            persistedObject.setString(PERSISTENCE_KEY_INTERACTION_SMTP_CONVERSATION, details.conversation());
        }

        // Custom data
        if (interaction.customData().isPresent()) {
            persistedObject.setString(PERSISTENCE_KEY_INTERACTION_CUSTOM_DATA, interaction.customData().get());
        }

        return persistedObject;
    }
}
