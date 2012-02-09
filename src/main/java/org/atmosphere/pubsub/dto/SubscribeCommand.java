package org.atmosphere.pubsub.dto;

import org.atmosphere.cpr.Broadcaster;

public class SubscribeCommand extends BaseCommand {

    @Override
    public void execute(Broadcaster broadcaster) {
        broadcaster.addAtmosphereResource(resource);
        resource.getBroadcaster().broadcast("joined channel " + channel);
    }
}
