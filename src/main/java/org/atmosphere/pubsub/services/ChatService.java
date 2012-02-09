package org.atmosphere.pubsub.services;

import com.google.common.collect.Maps;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.BroadcasterLifeCyclePolicyListener;
import org.atmosphere.cpr.DefaultBroadcaster;
import org.atmosphere.pubsub.dto.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChatService {
    @Autowired
    private BroadcasterFactory broadcasterFactory;
    private final static Logger LOG = LoggerFactory.getLogger(ChatService.class);
    private Map<String, Thread> runningPublishers = Maps.newConcurrentMap();

    public void execute(Command command) {
        Broadcaster broadcaster = broadcasterFactory.lookup(DefaultBroadcaster.class, command.getChannel(), true);
        command.execute(broadcaster);
        if (!broadcaster.isDestroyed() && !isRunningThreadOnChannel(command.getChannel())) {
            startMessagingThread(command.getChannel(), broadcaster);
        }
    }

    private synchronized boolean isRunningThreadOnChannel(String channel) {
        return runningPublishers.containsKey(channel) && runningPublishers.get(channel).isAlive();
    }

    private synchronized void startMessagingThread(String channel, Broadcaster broadcaster) {
        Thread thread = new Thread(new ChannelPublisher(broadcaster, channel));
        thread.start();
        runningPublishers.put(channel, thread);
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
                LOG.error("Interrupted chat broadcast thread", e);
            }
        }

        @Override
        public void onEmpty() {
            shouldRun = false;
            LOG.debug("Shutting down multicast thread for channel {}, no subscribers connected.", channel);
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
