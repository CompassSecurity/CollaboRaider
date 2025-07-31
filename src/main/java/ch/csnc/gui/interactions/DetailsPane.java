package ch.csnc.gui.interactions;

import burp.api.montoya.collaborator.InteractionType;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.*;
import ch.csnc.pingback.Pingback;

import javax.swing.*;
import java.util.HashMap;

public class DetailsPane extends JTabbedPane {
    DescriptionPanel descriptionViewer;
    HttpRequestEditor requestViewer;
    HttpResponseEditor responseViewer;
    HttpRequestEditor collaboratorHTTPRequestViewer;
    HttpResponseEditor collaboratorHTTPResponseViewer;
    WebSocketMessageEditor collaboratorDNSViewer;
    RawEditor collaboratorSMTPViewer;

    InteractionType previousType = null;
    HashMap<InteractionType, Integer> lastTabActive = new HashMap<>();


    TabController tabController;

    public DetailsPane(TabController tabController) {
        super();
        this.tabController = tabController;
        descriptionViewer = new DescriptionPanel();

        UserInterface userInterface = tabController.getUserInterface();

        requestViewer = userInterface.createHttpRequestEditor(EditorOptions.READ_ONLY);
        responseViewer = userInterface.createHttpResponseEditor(EditorOptions.READ_ONLY);

        collaboratorHTTPRequestViewer = userInterface.createHttpRequestEditor(EditorOptions.READ_ONLY);
        collaboratorHTTPResponseViewer = userInterface.createHttpResponseEditor(EditorOptions.READ_ONLY);

        collaboratorDNSViewer = userInterface.createWebSocketMessageEditor(EditorOptions.READ_ONLY,
                                                                           EditorOptions.SHOW_NON_PRINTABLE_CHARACTERS,
                                                                           EditorOptions.WRAP_LINES);

        collaboratorSMTPViewer = userInterface.createRawEditor(EditorOptions.READ_ONLY, EditorOptions.WRAP_LINES);

        addTab("Description", descriptionViewer);
        addTab("Request to server", requestViewer.uiComponent());
        addTab("Response from server", responseViewer.uiComponent());
    }

    public void updateDetails(Pingback pingback) {
        // Show original request and response that caused the pingback
        requestViewer.setRequest(pingback.request);
        responseViewer.setResponse(pingback.response);

        // Highlight Collaborator ID in request
        requestViewer.setSearchExpression(pingback.interaction.id().toString());

        // Populate the description tab
        descriptionViewer.setText(pingback.getDescription());

        // If interaction type changes, remove all tabs except the first three for description/request/response
        if (!pingback.interaction.type().equals(previousType)) {

            // Store index of current tab for this interaction type
            lastTabActive.put(previousType, getSelectedIndex());

            for (int i = getTabCount() - 1; i > 2; i--) {
                removeTabAt(i);
            }

            switch (pingback.interaction.type()) {
                case HTTP -> {
                    addTab("Request to Collaborator", collaboratorHTTPRequestViewer.uiComponent());
                    addTab("Response from Collaborator", collaboratorHTTPResponseViewer.uiComponent());
                }
                case DNS -> addTab("Raw DNS Query", collaboratorDNSViewer.uiComponent());
                case SMTP -> addTab("Raw SMTP Conversation", collaboratorSMTPViewer.uiComponent());
            }

            setSelectedIndex(lastTabActive.getOrDefault(pingback.interaction.type(), 0));
        }

        // Store current interaction type
        previousType = pingback.interaction.type();

        // Show details for HTTP pingback
        if (pingback.interaction.httpDetails().isPresent()) {
            collaboratorHTTPRequestViewer.setRequest(pingback.interaction.httpDetails()
                                                                         .get()
                                                                         .requestResponse()
                                                                         .request());
            collaboratorHTTPResponseViewer.setResponse(pingback.interaction.httpDetails()
                                                                           .get()
                                                                           .requestResponse()
                                                                           .response());
        }

        // Show details for DNS pingback
        else if (pingback.interaction.dnsDetails().isPresent()) {
            collaboratorDNSViewer.setContents(pingback.interaction.dnsDetails().get().query());
        }

        // Show details for SMTP pingback
        else if (pingback.interaction.smtpDetails().isPresent()) {
            collaboratorSMTPViewer.setContents(ByteArray.byteArray(pingback.interaction.smtpDetails()
                                                                                       .get()
                                                                                       .conversation()));
        }
    }
}
