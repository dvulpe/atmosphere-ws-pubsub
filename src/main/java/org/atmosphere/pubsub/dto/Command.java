package org.atmosphere.pubsub.dto;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;

public interface Command {
    void execute(Broadcaster broadcaster);

    String getChannel();

    void setResource(AtmosphereResource<?, ?> resource);
}
