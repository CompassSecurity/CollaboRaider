package ch.csnc.settings;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.CollaboratorPayload;
import burp.api.montoya.collaborator.SecretKey;
import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.persistence.PersistedObject;
import burp.api.montoya.persistence.Preferences;
import burp.api.montoya.scanner.audit.issues.AuditIssueSeverity;

import java.util.List;

public class SettingsModel {
    // Collaborator Client Polling Interval
    private final String PREFERENCES_KEY_POLLING_INTERVAL = "pollingInterval";
    // Burp Proxy highlight color
    private final String PREFERENCES_KEY_PROXY_HIGHLIGHT_COLOR = "proxyHighlightColor";
    // Burp Proxy annotate request with comment
    private final String PREFERENCES_KEY_ENABLE_COMMENTS = "enableComments";
    // Action if pingback came from own IP
    private final String PREFERENCES_KEY_ACTION_FOR_OWN_IP = "actionForOwnIP";
    // Severity rating for DNS pingback
    private final String PREFERENCES_KEY_SEVERITY_DNS = "pingbackSeverityDNS";
    private final String PREFERENCES_KEY_SEVERITY_HTTP = "pingbackSeverityHTTP";
    private final String PREFERENCES_KEY_SEVERITY_SMTP = "pingbackSeveritySMTP";
    // Save Collaborator secret key with project
    private final String KEY_COLLABORATOR_SECRET = "persistent_collaborator_secret_key";
    private final MontoyaApi montoyaApi;
    private final Preferences preferences;
    private final PersistedObject persistedObject;
    // Store own IP addresses discovered at startup
    private final OwnIPAddresses ownIPAddresses = new OwnIPAddresses();
    // Collaborator client is initialized either with a new secret key or with a key retrieved from project storage
    private final CollaboratorClient collaboratorClient;
    // Collaborator payload that is sent to determine the system's own external IP
    private CollaboratorPayload checkIpPayload;
    private String buildTime, version;

    public SettingsModel(MontoyaApi montoyaApi) {
        this.montoyaApi = montoyaApi;
        preferences = montoyaApi.persistence().preferences();
        persistedObject = montoyaApi.persistence().extensionData();

        // Create CollaboratorClient
        String storedCollaboratorKey = persistedObject.getString(KEY_COLLABORATOR_SECRET);
        if (storedCollaboratorKey == null) {
            collaboratorClient = montoyaApi.collaborator().createClient();
            String secretKey = collaboratorClient.getSecretKey().toString();
            persistedObject.setString(KEY_COLLABORATOR_SECRET, secretKey);
            montoyaApi.logging()
                      .logToOutput("Created new CollaboratorClient with secret key " + collaboratorClient.getSecretKey());
        } else {
            String secretKey = persistedObject.getString(KEY_COLLABORATOR_SECRET);
            collaboratorClient = montoyaApi.collaborator().restoreClient(SecretKey.secretKey(secretKey));
            montoyaApi.logging()
                      .logToOutput("Restored CollaboratorClient with existing secret key " + collaboratorClient.getSecretKey());
        }
        montoyaApi.logging().logToOutput("Collaborator server: " + collaboratorClient.server().address());
    }

    public OwnIPAddresses getOwnIPAddresses() {
        return ownIPAddresses;
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
            return AuditIssueSeverity.MEDIUM;
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
        String collaboratorURL = "http://" + checkIpPayload.toString();
        HttpRequest checkIPRequest = HttpRequest.httpRequestFromUrl(collaboratorURL);

        new Thread(() -> {
            montoyaApi.http().sendRequest(checkIPRequest);
            montoyaApi.logging().logToOutput("Sent request to " + collaboratorURL);
        }).start();
    }

    public CollaboratorClient getCollaboratorClient() {
        return collaboratorClient;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(String buildTime) {
        this.buildTime = buildTime;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public enum ActionForOwnIP {
        CONTINUE,
        REDUCED_RATING,
        DROP,
    }
}
