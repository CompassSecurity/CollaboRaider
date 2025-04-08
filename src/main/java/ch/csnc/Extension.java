package ch.csnc;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import ch.csnc.gui.MainTab;
import ch.csnc.pingback.PingbackHandler;
import ch.csnc.payload.PayloadsTableModel;
import ch.csnc.settings.SettingsModel;

import java.io.InputStream;
import java.util.Properties;

public class Extension implements BurpExtension {
    public static final String name = "CollaboRaider";

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
        SettingsModel settingsModel = new SettingsModel(montoyaApi);

        // Show build date
        try {
            InputStream inputStream = getClass().getResourceAsStream("/build-time.properties");
            Properties props = new Properties();
            props.load(inputStream);
            String buildTime = props.getProperty("build.time");
            String version = props.getProperty("build.version");
            logging.logToOutput("Version: " + version);
            logging.logToOutput("Build time: " + buildTime);

            settingsModel.setVersion(version);
            settingsModel.setBuildTime(buildTime);
        } catch (Exception e) {
            logging.logToError("Error loading build time. Could not find the file build-time.properties.");
        }


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

        // Create data model for payloads
        PayloadsTableModel payloadsTableModel = new PayloadsTableModel(montoyaApi.persistence().preferences());

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
                                                                                payloadsTableModel.getPayloads()));

        // Register unload callback
        montoyaApi.extension().registerUnloadingHandler(() -> {
            backgroundPoll.stop();
            logging.logToOutput("kthxbye.");
        });
    }


}