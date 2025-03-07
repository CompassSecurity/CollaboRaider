package ch.csnc;

import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.Interaction;
import burp.api.montoya.logging.Logging;
import ch.csnc.interaction.PingbackHandler;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BackgroundPoll {
    private final CollaboratorClient collaboratorClient;
    private final Logging logging;
    private final PingbackHandler pingbackHandler;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private ScheduledFuture<?> schedule;

    public BackgroundPoll(CollaboratorClient collaboratorClient, Logging logging, PingbackHandler pingbackHandler) {
        this.collaboratorClient = collaboratorClient;
        this.logging = logging;
        this.pingbackHandler = pingbackHandler;
    }

    public void execute() {
        // logging.logToOutput("Polling...");
        List<Interaction> interactions = collaboratorClient.getAllInteractions();
        for (Interaction interaction : interactions) {
            pingbackHandler.handleInteraction(interaction);
        }
    }

    public void start() {
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        schedule = scheduledThreadPoolExecutor.scheduleAtFixedRate(this::execute,0, 5, TimeUnit.SECONDS);
    }

    public void stop() {
        schedule.cancel(true);
        scheduledThreadPoolExecutor.shutdown();
        logging.logToOutput("BackgroundPoll stopped");
    }


}
