package org.atmosphere.pubsub.config.protocol;

import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResourceImpl;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.cpr.AtmosphereServlet;
import org.atmosphere.pubsub.config.SpringApplicationContext;
import org.atmosphere.pubsub.dto.BaseCommand;
import org.atmosphere.pubsub.dto.Command;
import org.atmosphere.pubsub.dto.EmptyCommand;
import org.atmosphere.pubsub.services.ChatService;
import org.atmosphere.websocket.WebSocket;
import org.atmosphere.websocket.WebSocketProcessor;
import org.atmosphere.websocket.WebSocketProtocol;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class DelegatingWebSocketProtocol implements WebSocketProtocol {

    public static final Logger LOG = LoggerFactory.getLogger(DelegatingWebSocketProtocol.class);

    @Override
    public void configure(AtmosphereServlet.AtmosphereConfig atmosphereConfig) {
        // nothing needed
    }

    @Override
    public List<AtmosphereRequest> onMessage(WebSocket webSocket, String message) {
        if (webSocket.resource() == null) {
            return null;
        }
        AtmosphereResourceImpl resource = (AtmosphereResourceImpl) webSocket.resource();
        resource.suspend();

        ChatService chatService = SpringApplicationContext.getBean(ChatService.class);
        ObjectMapper mapper = SpringApplicationContext.getBean(ObjectMapper.class);
        Command command = readCommand(message, mapper);
        command.setResource(resource);
        chatService.execute(command);
        return null;
    }

    private Command readCommand(String s, ObjectMapper mapper) {
        Command command = new EmptyCommand();
        try {
            command = mapper.readValue(s, BaseCommand.class);
        } catch (IOException e) {
            LOG.error("Exception converting JSON:", e);
        }
        return command;
    }

    @Override
    public List<AtmosphereRequest> onMessage(WebSocket webSocket, byte[] bytes, int offset, int length) {
        return onMessage(webSocket, new String(bytes, offset, length));
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        LOG.debug("opened web socket connection {}", webSocket);
    }

    @Override
    public void onClose(WebSocket webSocket) {
        LOG.debug("closing web socket connection {}", webSocket);
    }

    @Override
    public void onError(WebSocket webSocket, WebSocketProcessor.WebSocketException e) {
        LOG.error("error on websocket connection {}", e);
    }

    @Override
    public boolean inspectResponse() {
        return false;
    }

    @Override
    public String handleResponse(AtmosphereResponse<?> atmosphereResponse, String message) {
        return message;
    }

    @Override
    public byte[] handleResponse(AtmosphereResponse<?> atmosphereResponse, byte[] message, int offset,
                                 int length) {
        return message;
    }
}