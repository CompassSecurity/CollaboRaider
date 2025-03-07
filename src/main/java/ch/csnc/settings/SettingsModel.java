package ch.csnc.settings;

import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.persistence.Preferences;

public class SettingsModel {
    static SettingsModel instance;

    // Collaborator Client Polling Interval
    public int collaboratorPollingInterval = 5;
    private final String PREFERENCES_KEY_POLLING_INTERVAL = "pollingInterval";

    // Highlight Color in Burp Proxy
    public HighlightColor proxyHighlightColor = HighlightColor.RED;
    private final String PREFERENCES_KEY_PROXY_HIGHLIGHT_COLOR = "proxyHighlightColor";

    // Handle pingbacks from own IP
    public ActionForOwnIP actionForOwnIP = ActionForOwnIP.CONTINUE;
    private final String PREFERENCES_KEY_ACTION_FOR_OWN_IP = "actionForOwnIP";

    public SettingsModel() {

    }

    public static SettingsModel getInstance() {
        if (instance == null) {
            instance = new SettingsModel();
        }
        return instance;
    }


    // Restore settings
    public void getFromPreferenceStore(Preferences preferences) {
        if (preferences.getInteger(PREFERENCES_KEY_POLLING_INTERVAL) != null) {
            collaboratorPollingInterval = preferences.getInteger(PREFERENCES_KEY_POLLING_INTERVAL);
        }

        if (preferences.getString(PREFERENCES_KEY_PROXY_HIGHLIGHT_COLOR) != null) {
            proxyHighlightColor = HighlightColor.valueOf(preferences.getString(PREFERENCES_KEY_PROXY_HIGHLIGHT_COLOR));
        }

        if (preferences.getString(PREFERENCES_KEY_ACTION_FOR_OWN_IP) != null) {
            actionForOwnIP = ActionForOwnIP.valueOf(preferences.getString(PREFERENCES_KEY_ACTION_FOR_OWN_IP));
        }
    }

    // Store settings
    public void saveToPreferenceStore(Preferences preferences) {
        preferences.setInteger(PREFERENCES_KEY_POLLING_INTERVAL, collaboratorPollingInterval);
        preferences.setString(PREFERENCES_KEY_PROXY_HIGHLIGHT_COLOR, proxyHighlightColor.toString());
        preferences.setString(PREFERENCES_KEY_ACTION_FOR_OWN_IP, actionForOwnIP.toString());
    }

    public enum ActionForOwnIP {
        CONTINUE,
        REDUCED_RATING,
        DROP,
    }
}
