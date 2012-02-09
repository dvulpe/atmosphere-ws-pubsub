package org.atmosphere.pubsub.dto;

import org.atmosphere.cpr.AtmosphereResource;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = UnsubscribeCommand.class, name = "unsubscribe"),
        @JsonSubTypes.Type(value = SubscribeCommand.class, name = "subscribe")})
public abstract class BaseCommand implements Command {
    protected AtmosphereResource resource;
    protected String channel;

    @Override
    public void setResource(AtmosphereResource resource) {
        this.resource = resource;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
