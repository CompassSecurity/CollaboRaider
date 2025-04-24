package ch.csnc;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.SecretKey;
import burp.api.montoya.logging.Logging;
import ch.csnc.gui.MainTab;
import ch.csnc.payload.PayloadsTableModel;
import ch.csnc.pingback.PingbackHandler;
import ch.csnc.settings.SettingsModel;

import javax.swing.*;
import java.io.InputStream;
import java.util.Properties;

public class Extension implements BurpExtension {
    public static final String name = "Collaborator Everywhere";
    private Logging logging;

    @Override
    public void initialize(MontoyaApi montoyaApi) {
        // Initialize extension
        montoyaApi.extension().setName(name);

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

        // Create collaborator client
        String storedCollaboratorKey = settingsModel.getCollaboratorSecret();
        CollaboratorClient collaboratorClient;
        try {
            if (storedCollaboratorKey == null) {
                collaboratorClient = montoyaApi.collaborator().createClient();
                String secretKey = collaboratorClient.getSecretKey().toString();
                settingsModel.setCollaboratorSecret(secretKey);
                montoyaApi.logging()
                          .logToOutput("Created new CollaboratorClient with secret key " + collaboratorClient.getSecretKey());
            } else {
                collaboratorClient = montoyaApi.collaborator()
                                               .restoreClient(SecretKey.secretKey(storedCollaboratorKey));
                montoyaApi.logging()
                          .logToOutput("Restored CollaboratorClient with existing secret key " + collaboratorClient.getSecretKey());
            }
            montoyaApi.logging().logToOutput("Collaborator server: " + collaboratorClient.server().address());
            // Add to settings
            settingsModel.addCollaboratorClient(collaboratorClient);
        } catch (Exception e) {
            // If Burp Collaborator is disabled, an exception is thrown
            String errorMessage = "Burp Collaborator is currently disabled.\n" +
                    "Please go to Settings -> Project -> Collaborator to enable it, then reload the extension.";
            montoyaApi.logging().logToOutput(errorMessage);

            JOptionPane.showOptionDialog(montoyaApi.userInterface().swingUtils().suiteFrame(),
                                         errorMessage,
                                         "Collaborator Unavailable",
                                         JOptionPane.DEFAULT_OPTION,
                                         JOptionPane.ERROR_MESSAGE,
                                         null,
                                         null,
                                         null);
            return;
        }


        // Check own IP by sending a request to the Collaborator server
        // Catch UnknownHostException and display a message if the lookup fails
        try {
            settingsModel.sendCheckIpPayload();
        } catch (Exception e) {
            String errorMessage = "The domain " + settingsModel.getCheckIpPayload()
                                                               .toString() + " could not be resolved.\n" +
                    "Please check your connection, then reload the extension.";
            montoyaApi.logging().logToOutput(errorMessage);
            JOptionPane.showOptionDialog(montoyaApi.userInterface().swingUtils().suiteFrame(),
                                         errorMessage,
                                         "Connection error",
                                         JOptionPane.DEFAULT_OPTION,
                                         JOptionPane.ERROR_MESSAGE,
                                         null,
                                         null,
                                         null);
            return;
        }


        // Create Interaction handler which processes events
        PingbackHandler pingbackHandler = new PingbackHandler(montoyaApi,
                                                              settingsModel);


        // Polling
        BackgroundPoll backgroundPoll = new BackgroundPoll(collaboratorClient,
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
                                                                                collaboratorClient,
                                                                                payloadsTableModel));

        // Register unload callback
        montoyaApi.extension().registerUnloadingHandler(() -> {
            backgroundPoll.stop();
            logging.logToOutput("kthxbye.");
        });
    }


}