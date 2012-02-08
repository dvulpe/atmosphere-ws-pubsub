package org.atmosphere.pubsub.services;

import com.google.common.collect.Maps;
import org.atmosphere.cpr.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Service
public class ChatService {
    @Autowired
    private BroadcasterFactory broadcasterFactory;
    private final static Logger LOG = LoggerFactory.getLogger(ChatService.class);
    private Map<String, Thread> runningPublishers = Maps.newConcurrentMap();

    public void subscribe(AtmosphereResource<HttpServletRequest, HttpServletResponse> resource, String channel) {
        Broadcaster broadcaster = broadcasterFactory.lookup(DefaultBroadcaster.class, channel, true);
        LOG.debug("Subscribing resource {} to channel {}.", resource, channel);
        broadcaster.addAtmosphereResource(resource);
        if (!isRunningThreadOnChannel(channel)) {
            Thread thread = new Thread(new ChannelPublisher(broadcaster, channel));
            thread.start();
            runningPublishers.put(channel, thread);
        }
    }

    private synchronized boolean isRunningThreadOnChannel(String channel) {
        return runningPublishers.containsKey(channel) && runningPublishers.get(channel).isAlive();
    }

    public void unsubscribe(AtmosphereResource<HttpServletRequest, HttpServletResponse> resource, String channel) {
        Broadcaster broadcaster = broadcasterFactory.lookup(DefaultBroadcaster.class, channel, false);
        if (broadcaster != null) {
            LOG.debug("De-subscribing resource {} from channel {}.", resource, channel);
            broadcaster.removeAtmosphereResource(resource);
        }
    }

    public class ChannelPublisher implements Runnable, BroadcasterLifeCyclePolicyListener {
        private final Broadcaster broadcaster;
        private int counter;
        private boolean shouldRun = true;
        private final String channel;

        public ChannelPublisher(Broadcaster broadcaster, String channel) {
            this.broadcaster = broadcaster;
            this.channel = channel;
            broadcaster.addBroadcasterLifeCyclePolicyListener(this);
        }

        @Override
        public void run() {
            while (shouldRun) {
                counter++;
                broadcaster.broadcast("counter " + counter + ", channel: " + channel);
                await();
            }
            runningPublishers.remove(channel);
        }

        private void await() {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onEmpty() {
            shouldRun = false;
            LOG.debug("Disabling multicast thread, no subscribers connected");
        }

        @Override
        public void onIdle() {
            //nothing needed
        }

        @Override
        public void onDestroy() {
            shouldRun = false;
        }
    }

}
