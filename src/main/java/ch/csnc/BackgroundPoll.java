package ch.csnc;

import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.Interaction;
import burp.api.montoya.logging.Logging;
import ch.csnc.pingback.PingbackHandler;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BackgroundPoll {
    private final CollaboratorClient collaboratorClient;
    private final Logging logging;
    private final PingbackHandler pingbackHandler;
    private final int pollingInterval;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private ScheduledFuture<?> schedule;

    public BackgroundPoll(CollaboratorClient collaboratorClient,
                          Logging logging,
                          PingbackHandler pingbackHandler,
                          int pollingInterval) {
        this.collaboratorClient = collaboratorClient;
        this.logging = logging;
        this.pingbackHandler = pingbackHandler;
        this.pollingInterval = pollingInterval;
    }

    public void execute() {
        if (!schedule.isCancelled()) {
            List<Interaction> interactions = collaboratorClient.getAllInteractions();
            // logging.logToOutput("Polling. Got %d interactions".formatted(interactions.size()));
            for (Interaction interaction : interactions) {
                pingbackHandler.handleInteraction(interaction);
            }
        }
    }

    public void start() {
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        schedule = scheduledThreadPoolExecutor.scheduleAtFixedRate(this::execute, 0, pollingInterval, TimeUnit.SECONDS);
        logging.logToOutput("Start polling every %d seconds.".formatted(pollingInterval));
    }

    public void stop() {
        schedule.cancel(true);
        scheduledThreadPoolExecutor.shutdown();
        logging.logToOutput("BackgroundPoll stopped");
    }


}
