package org.atmosphere.pubsub.dto;

import org.atmosphere.cpr.Broadcaster;

public class EmptyCommand extends BaseCommand {
    @Override
    public void execute(Broadcaster broadcaster) {
        // nothing needed
    }
}
