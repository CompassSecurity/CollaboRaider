package ch.csnc;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.persistence.Preferences;
import ch.csnc.gui.MainTab;
import ch.csnc.interaction.PingbackHandler;
import ch.csnc.payload.Payload;
import ch.csnc.payload.PayloadType;
import ch.csnc.payload.PayloadsTableModel;
import ch.csnc.settings.SettingsModel;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Extension implements BurpExtension {
    public static final String name = "CollaboRaider";
    public static final String version = "0.1";

    private MontoyaApi montoyaApi;
    private Logging logging;

    @Override
    public void initialize(MontoyaApi montoyaApi) {
        // Initialize extension
        montoyaApi.extension().setName(name);
        this.montoyaApi = montoyaApi;

        // Write a message to output stream
        logging = montoyaApi.logging();
        logging.logToOutput("Extension loaded. Happy hacking!");

        // Load Settings
        Preferences preferences = montoyaApi.persistence().preferences();
        SettingsModel settingsModel = new SettingsModel(montoyaApi);

        // Show build date
        try {
            InputStream inputStream = getClass().getResourceAsStream("/build-time.properties");
            Properties props = new Properties();
            props.load(inputStream);
            String buildTime = props.getProperty("build.time");
            logging.logToOutput("Build time: " + buildTime);
            settingsModel.setBuildTime(buildTime);
        } catch (Exception e) {
            logging.logToError("Error loading build time. Could not find the file build-time.properties.");
        }



        List<Payload> payloadSettings;
        if (preferences.getInteger("KEY_NUM_ROWS") != null) {
            payloadSettings = loadPayloadsFromPersistence(preferences);
        } else {
            payloadSettings = loadStoredPayloads();
        }

        PayloadsTableModel payloadsTableModel = new PayloadsTableModel(payloadSettings);
        // Get notified if something changes and store the table
        payloadsTableModel.addTableModelListener(e -> {
            logging.logToOutput("TableModelEvent " + e.getType());
            int numRows = payloadsTableModel.getRowCount();
            preferences.setInteger("KEY_NUM_ROWS", numRows);
            for (int i = 0; i < numRows; ++i) {
                preferences.setString("KEY_ROWS_" + i, payloadsTableModel.get(i).toString());
            }
        });

        // Check own IP by sending a request to the Collaborator server
        settingsModel.sendCheckIpPayload();


        // Create Interaction handler which processes events
        PingbackHandler pingbackHandler = new PingbackHandler(montoyaApi,
                                                              settingsModel);

        // Polling
        BackgroundPoll backgroundPoll = new BackgroundPoll(settingsModel.getCollaboratorClient(),
                                                           logging,
                                                           pingbackHandler,
                                                           settingsModel.getCollaboratorPollingInterval());
        backgroundPoll.start();

        // Register new tab in UI
        montoyaApi.userInterface()
                  .registerSuiteTab(name,
                                    new MainTab(montoyaApi,
                                                pingbackHandler.getTableModel(),
                                                payloadsTableModel,
                                                settingsModel));

        // Register Proxy handler
        montoyaApi.proxy().registerRequestHandler(new CustomProxyRequestHandler(logging,
                                                                                settingsModel.getCollaboratorClient(),
                                                                                payloadSettings));

        // Register unload callback
        montoyaApi.extension().registerUnloadingHandler(() -> {
            backgroundPoll.stop();
            logging.logToOutput("kthxbye.");
        });
    }

    public List<Payload> loadPayloadsFromPersistence(Preferences preferences) {
        int numRows = preferences.getInteger("KEY_NUM_ROWS");
        List<Payload> settings = new ArrayList<>(numRows);
        for (int i = 0; i < numRows; ++i) {
            String serialized = preferences.getString("KEY_ROWS_" + i);
            // logging.logToOutput("restore " + serialized);
            Payload payload = Payload.fromString(serialized);
            settings.add(i, payload);
        }
        return settings;
    }

    public List<Payload> loadStoredPayloads() {
        List<Payload> settings = new ArrayList<>();

        // Read resource file and add all items to list
        InputStream injectionResource = getClass().getResourceAsStream("/injections");
        if (injectionResource != null) {
            Scanner s = new Scanner(injectionResource, StandardCharsets.UTF_8).useDelimiter("\\n");
            while (s.hasNextLine()) {
                String line = s.nextLine();

                // Comments start with ';'
                if (line.startsWith(";"))
                    continue;

                // Inactive parameters are marked with '#'
                // Not ideal, but it works™
                // -> need a better solution
                Boolean isActive = Boolean.TRUE;
                if (line.startsWith("#")) {
                    isActive = Boolean.FALSE;
                    line = line.substring(1);
                }

                String[] split = line.split(",", 3);

                PayloadType type = null;
                if (Objects.equals(split[0], "header")) {
                    type = PayloadType.HEADER;
                }
                if (Objects.equals(split[0], "param")) {
                    type = PayloadType.PARAM;
                }

                if (type == null) {
                    logging.logToOutput("Invalid injection point: " + line);
                    continue;
                }

                settings.add(new Payload(isActive, type, split[1], split[2]));
                //logging.logToOutput("Added " + Arrays.toString(split) + ", active: " + isActive);
            }
            s.close();
        }

        return settings;
    }
}