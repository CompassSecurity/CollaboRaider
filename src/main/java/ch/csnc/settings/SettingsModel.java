package ch.csnc.settings;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.CollaboratorPayload;
import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.persistence.PersistedObject;
import burp.api.montoya.persistence.Preferences;
import burp.api.montoya.scanner.audit.issues.AuditIssueSeverity;
import burp.api.montoya.utilities.json.*;

import java.util.HashMap;
import java.util.stream.Collectors;

public class SettingsModel {
    private final String PREFERENCES_KEY_POLLING_INTERVAL = "pollingInterval";
    private final String PREFERENCES_KEY_PROXY_HIGHLIGHT_COLOR = "proxyHighlightColor";
    private final String PREFERENCES_KEY_ENABLE_COMMENTS = "enableComments";
    private final String PREFERENCES_KEY_ACTION_FOR_OWN_IP = "actionForOwnIP";
    private final String PREFERENCES_KEY_SEVERITY_DNS = "pingbackSeverityDNS";
    private final String PREFERENCES_KEY_SEVERITY_HTTP = "pingbackSeverityHTTP";
    private final String PREFERENCES_KEY_SEVERITY_SMTP = "pingbackSeveritySMTP";
    private final String KEY_COLLABORATOR_SECRET = "persistent_collaborator_secret_key";

    private final MontoyaApi montoyaApi;
    private final Preferences preferences;
    private final PersistedObject persistedObject;
    private final OwnIPAddresses ownIPAddresses = new OwnIPAddresses();
    private CollaboratorClient collaboratorClient;
    private CollaboratorPayload checkIpPayload;
    private String buildTime, version;

    public SettingsModel(MontoyaApi montoyaApi) {
        this.montoyaApi = montoyaApi;
        preferences = montoyaApi.persistence().preferences();
        persistedObject = montoyaApi.persistence().extensionData();
    }

    public OwnIPAddresses getOwnIPAddresses() {
        return ownIPAddresses;
    }

    public String getCollaboratorSecret() {
        return persistedObject.getString(KEY_COLLABORATOR_SECRET);
    }

    public void setCollaboratorSecret(String secretKey) {
        persistedObject.setString(KEY_COLLABORATOR_SECRET, secretKey);
    }

    public boolean getCommentsEnabled() {
        if (preferences.getBoolean(PREFERENCES_KEY_ENABLE_COMMENTS) != null) {
            return preferences.getBoolean(PREFERENCES_KEY_ENABLE_COMMENTS);
        } else {
            // Default value
            return true;
        }
    }

    public void setCommentsEnabled(boolean commentsEnabled) {
        preferences.setBoolean(PREFERENCES_KEY_ENABLE_COMMENTS, commentsEnabled);
    }

    public HighlightColor getProxyHighlightColor() {
        if (preferences.getString(PREFERENCES_KEY_PROXY_HIGHLIGHT_COLOR) != null) {
            return HighlightColor.valueOf(preferences.getString(PREFERENCES_KEY_PROXY_HIGHLIGHT_COLOR));
        } else {
            // Default value
            return HighlightColor.RED;
        }
    }

    public void setProxyHighlightColor(HighlightColor proxyHighlightColor) {
        preferences.setString(PREFERENCES_KEY_PROXY_HIGHLIGHT_COLOR, proxyHighlightColor.toString());
    }

    public int getCollaboratorPollingInterval() {
        if (preferences.getInteger(PREFERENCES_KEY_POLLING_INTERVAL) != null) {
            return preferences.getInteger(PREFERENCES_KEY_POLLING_INTERVAL);
        } else {
            // Default value
            return 5;
        }
    }

    public void setCollaboratorPollingInterval(int pollingInterval) {
        preferences.setInteger(PREFERENCES_KEY_POLLING_INTERVAL, pollingInterval);
    }

    public ActionForOwnIP getActionForOwnIP() {
        if (preferences.getString(PREFERENCES_KEY_ACTION_FOR_OWN_IP) != null) {
            return ActionForOwnIP.valueOf(preferences.getString(PREFERENCES_KEY_ACTION_FOR_OWN_IP));
        } else {
            // Default value
            return ActionForOwnIP.CONTINUE;
        }
    }

    public void setActionForOwnIP(String actionForOwnIP) {
        preferences.setString(PREFERENCES_KEY_ACTION_FOR_OWN_IP, actionForOwnIP);
    }

    public AuditIssueSeverity getPingbackSeverityDNS() {
        if (preferences.getString(PREFERENCES_KEY_SEVERITY_DNS) != null) {
            return AuditIssueSeverity.valueOf(preferences.getString(PREFERENCES_KEY_SEVERITY_DNS));
        } else {
            // Default value
            return AuditIssueSeverity.MEDIUM;
        }
    }

    public void setPingbackSeverityDNS(AuditIssueSeverity severity) {
        preferences.setString(PREFERENCES_KEY_SEVERITY_DNS, severity.toString());
    }

    public AuditIssueSeverity getPingbackSeverityHTTP() {
        if (preferences.getString(PREFERENCES_KEY_SEVERITY_HTTP) != null) {
            return AuditIssueSeverity.valueOf(preferences.getString(PREFERENCES_KEY_SEVERITY_HTTP));
        } else {
            // Default value
            return AuditIssueSeverity.HIGH;
        }
    }

    public void setPingbackSeverityHTTP(AuditIssueSeverity severity) {
        preferences.setString(PREFERENCES_KEY_SEVERITY_HTTP, severity.toString());
    }

    public AuditIssueSeverity getPingbackSeveritySMTP() {
        if (preferences.getString(PREFERENCES_KEY_SEVERITY_SMTP) != null) {
            return AuditIssueSeverity.valueOf(preferences.getString(PREFERENCES_KEY_SEVERITY_SMTP));
        } else {
            // Default value
            return AuditIssueSeverity.HIGH;
        }
    }

    public void setPingbackSeveritySMTP(AuditIssueSeverity severity) {
        preferences.setString(PREFERENCES_KEY_SEVERITY_SMTP, severity.toString());
    }

    public CollaboratorPayload getCheckIpPayload() {
        return checkIpPayload;
    }

    public void setCheckIpPayload(CollaboratorPayload checkIpPayload) {
        this.checkIpPayload = checkIpPayload;
    }

    public void sendCheckIpPayload() {
        ownIPAddresses.init();
        checkIpPayload = collaboratorClient.generatePayload();
        String collaboratorURL = "https://" + checkIpPayload.toString();
        HttpRequest checkIPRequest = HttpRequest.httpRequestFromUrl(collaboratorURL);

        montoyaApi.logging().logToOutput("Sending initial request to " + collaboratorURL);
        montoyaApi.http().sendRequest(checkIPRequest);
    }

    public void addCollaboratorClient(CollaboratorClient collaboratorClient) {
        this.collaboratorClient = collaboratorClient;
    }

    public String getCollaboratorAddress() {
        return this.collaboratorClient.server().address().toLowerCase();
    }

    public String getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(String buildTime) {
        this.buildTime = buildTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String serialize() {
        JsonObjectNode jsonObject = JsonObjectNode.jsonObjectNode();
        jsonObject.putNumber(PREFERENCES_KEY_POLLING_INTERVAL, getCollaboratorPollingInterval());
        jsonObject.putString(PREFERENCES_KEY_PROXY_HIGHLIGHT_COLOR, getProxyHighlightColor().name());
        jsonObject.putBoolean(PREFERENCES_KEY_ENABLE_COMMENTS, getCommentsEnabled());
        jsonObject.putString(PREFERENCES_KEY_ACTION_FOR_OWN_IP, getActionForOwnIP().name());
        jsonObject.putString(PREFERENCES_KEY_SEVERITY_DNS, getPingbackSeverityDNS().name());
        jsonObject.putString(PREFERENCES_KEY_SEVERITY_HTTP, getPingbackSeverityHTTP().name());
        jsonObject.putString(PREFERENCES_KEY_SEVERITY_SMTP, getPingbackSeveritySMTP().name());
        return jsonObject.toJsonString();
    }

    public void fromJson(String data) {
        JsonObjectNode jsonNode = JsonNode.jsonNode(data).asObject();

        Number pollingInterval = jsonNode.getNumber(PREFERENCES_KEY_POLLING_INTERVAL);
        if (pollingInterval != null)
            setCollaboratorPollingInterval(pollingInterval.intValue());

        String proxyHighlightColor = jsonNode.getString(PREFERENCES_KEY_PROXY_HIGHLIGHT_COLOR);
        if (proxyHighlightColor != null)
            setProxyHighlightColor(HighlightColor.valueOf(proxyHighlightColor));

        Boolean enableComments = jsonNode.getBoolean(PREFERENCES_KEY_ENABLE_COMMENTS);
        if (enableComments != null)
            setCommentsEnabled(enableComments);

        String actionForOwnIP = jsonNode.getString(PREFERENCES_KEY_ACTION_FOR_OWN_IP);
        if (actionForOwnIP != null)
            setActionForOwnIP(actionForOwnIP);

        String severityDNS = jsonNode.getString(PREFERENCES_KEY_SEVERITY_DNS);
        if (severityDNS != null)
            setPingbackSeverityDNS(AuditIssueSeverity.valueOf(severityDNS));

        String severityHTTP = jsonNode.getString(PREFERENCES_KEY_SEVERITY_HTTP);
        if (severityHTTP != null)
            setPingbackSeverityHTTP(AuditIssueSeverity.valueOf(severityHTTP));

        String severitySMTP = jsonNode.getString(PREFERENCES_KEY_SEVERITY_SMTP);
        if (severitySMTP != null)
            setPingbackSeveritySMTP(AuditIssueSeverity.valueOf(severitySMTP));
    }

    public enum ActionForOwnIP {
        CONTINUE,
        REDUCED_RATING,
        DROP,
    }
}
