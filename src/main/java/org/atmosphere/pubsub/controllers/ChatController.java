package org.atmosphere.pubsub.controllers;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter;
import org.atmosphere.pubsub.services.ChatService;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Controller
public class ChatController {
    @Autowired
    private ChatService chatService;
    private final static Logger LOG = LoggerFactory.getLogger(ChatController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping("/websockets")
    @ResponseBody
    public void subscribeToDmx(HttpServletRequest request,
                               AtmosphereResource<HttpServletRequest, HttpServletResponse> resource) throws IOException, InterruptedException {
        resource.getResponse().setContentType("text/html");
        suspend(resource);
        try {
            CommandObject commandObject = objectMapper.readValue(request.getInputStream(), CommandObject.class);
            if (commandObject != null) {
                if ("subscribe".equals(commandObject.command)) {
                    chatService.subscribe(resource, commandObject.channel);
                } else if ("unsubscribe".equals(commandObject.command)) {
                    chatService.unsubscribe(resource, commandObject.channel);
                }
            }
        } catch (IOException ex) {
            // nothing needed
        }
    }

    private void suspend(final AtmosphereResource<HttpServletRequest, HttpServletResponse> resource) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        resource.addEventListener(new AtmosphereResourceEventListenerAdapter() {
            @Override
            public void onSuspend(AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event) {
                countDownLatch.countDown();
                resource.removeEventListener(this);
            }
        });
        resource.suspend();
        countDownLatch.await();
    }

    public static class CommandObject {
        private String command;
        private String channel;

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }
    }

}
