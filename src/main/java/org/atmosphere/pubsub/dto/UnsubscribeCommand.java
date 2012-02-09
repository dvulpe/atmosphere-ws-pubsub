package org.atmosphere.pubsub.dto;

import org.atmosphere.cpr.Broadcaster;

public class UnsubscribeCommand extends BaseCommand {

    @Override
    public void execute(Broadcaster broadcaster) {
        broadcaster.removeAtmosphereResource(resource);
        resource.getBroadcaster().broadcast("left channel " + channel);
    }
}
