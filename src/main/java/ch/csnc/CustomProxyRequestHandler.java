package ch.csnc;

import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.http.message.params.HttpParameter;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;
import ch.csnc.payload.Payload;

import java.util.*;

public class CustomProxyRequestHandler implements ProxyRequestHandler {
    private final Logging logging;
    private final CollaboratorClient collaboratorClient;
    private final List<Payload> payloads;


    public CustomProxyRequestHandler(Logging logging, CollaboratorClient collaboratorClient, List<Payload> payloads) {
        this.logging = logging;
        this.collaboratorClient = collaboratorClient;
        this.payloads = payloads;

        logging.logToOutput("Custom proxy request handler created.");
    }


    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        return ProxyRequestReceivedAction.continueWith(interceptedRequest);
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        // Ignore out-of-scope requests
        if (!interceptedRequest.isInScope())
            return ProxyRequestToBeSentAction.continueWith(interceptedRequest);

        HttpRequest newRequest = interceptedRequest.withHeader("Cache-Control", "no-transform");

        for (Payload payload : payloads) {
            // Only consider active items
            if (!payload.isActive)
                continue;

            String target = payload.getKey();

            // Apply payload replacement rules
            String value = payload.getValue()
                                  .replace("%s", collaboratorClient.generatePayload().toString())
                                  .replace("%h", interceptedRequest.headerValue("Host"));
            if (interceptedRequest.hasHeader("Origin"))
                value = value.replace("%o", interceptedRequest.headerValue("Origin"));
            if (interceptedRequest.hasHeader("Referer"))
                value = value.replace("%r", interceptedRequest.headerValue("Referer"));


            switch (payload.getType()) {
                case HEADER:
                    newRequest = newRequest.withHeader(target, value);
                    break;
                case PARAM:
                    HttpParameter injectedUrlParam = HttpParameter.urlParameter(target, value);
                    newRequest = newRequest.withParameter(injectedUrlParam);
                    break;
                default:
                    break;
            }
        }

        return ProxyRequestToBeSentAction.continueWith(newRequest);
    }

}
