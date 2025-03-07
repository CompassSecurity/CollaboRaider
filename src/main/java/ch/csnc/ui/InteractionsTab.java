package ch.csnc.ui;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.HttpResponseEditor;
import burp.api.montoya.ui.editor.RawEditor;
import ch.csnc.interaction.PingbackTableModel;
import ch.csnc.interaction.Pingback;

import javax.swing.*;


import static burp.api.montoya.ui.editor.EditorOptions.READ_ONLY;

public class InteractionsTab extends JSplitPane {
    public final String TAB_TITLE = "Interactions";
    private final MontoyaApi api;

    public InteractionsTab(MontoyaApi api, PingbackTableModel tableModel) {
        this.api = api;
        setName(TAB_TITLE);
        constructLoggerTab(tableModel);
    }

    private void constructLoggerTab(PingbackTableModel tableModel)
    {
        // main split pane
        this.setOrientation(JSplitPane.VERTICAL_SPLIT);

        // tabs with request/response viewers
        JTabbedPane tabs = new JTabbedPane();

        UserInterface userInterface = api.userInterface();

        HttpRequestEditor requestViewer = userInterface.createHttpRequestEditor(READ_ONLY);
        HttpResponseEditor responseViewer = userInterface.createHttpResponseEditor(READ_ONLY);
        RawEditor collaboratorRequestViewer = userInterface.createRawEditor(READ_ONLY);
        RawEditor collaboratorResponseViewer = userInterface.createRawEditor(READ_ONLY);

        tabs.addTab("Request to server", requestViewer.uiComponent());
        tabs.addTab("Response to server", responseViewer.uiComponent());
        tabs.addTab("Request to Collaborator", collaboratorRequestViewer.uiComponent());
        tabs.addTab("Response from Collaborator", collaboratorResponseViewer.uiComponent());

        this.setRightComponent(tabs);

        // table of log entries
        JScrollPane scrollPane = getJScrollPane(tableModel, requestViewer, responseViewer, collaboratorRequestViewer, collaboratorResponseViewer);

        this.setLeftComponent(scrollPane);
    }

    JTable table;
    private JScrollPane getJScrollPane(PingbackTableModel tableModel, HttpRequestEditor requestViewer, HttpResponseEditor responseViewer, RawEditor collaboratorRequestViewer, RawEditor collaboratorResponseViewer) {
        table = new JTable(tableModel)
        {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend)
            {
                // show the log entry for the selected row
                Pingback pingback = tableModel.get(rowIndex);

                // Show original request and response that caused the pingback
                requestViewer.setRequest(pingback.request);
                responseViewer.setResponse(pingback.response);

                requestViewer.setSearchExpression(pingback.interaction.id().toString());

                // Show request that was sent to Collaborator
                ByteArray collaboratorRequest = ByteArray.byteArray("");
                ByteArray collaboratorResponse = ByteArray.byteArray("");
                if (pingback.interaction.httpDetails().isPresent()) {
                    collaboratorRequest = ByteArray.byteArray(pingback.interaction.httpDetails().get().requestResponse().request().toString());
                    collaboratorResponse = ByteArray.byteArray(pingback.interaction.httpDetails().get().requestResponse().response().toString());
                }
                collaboratorRequestViewer.setContents(collaboratorRequest);
                collaboratorResponseViewer.setContents(collaboratorResponse);

                super.changeSelection(rowIndex, columnIndex, toggle, extend);
            }
        };

        return new JScrollPane(table);
    }
}
