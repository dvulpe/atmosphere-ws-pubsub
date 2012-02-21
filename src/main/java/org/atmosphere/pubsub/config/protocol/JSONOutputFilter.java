package org.atmosphere.pubsub.config.protocol;

import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.pubsub.config.SpringApplicationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;

public class JSONOutputFilter implements BroadcastFilter {

    private static final Logger LOG = LoggerFactory.getLogger(JSONOutputFilter.class);

    @Override
    public BroadcastAction filter(Object originalMessage, Object message) {
        if (message instanceof String) {
            return new BroadcastAction(message);
        }
        ObjectMapper objectMapper = SpringApplicationContext.getBean(ObjectMapper.class);
        return new BroadcastAction(writeAsString(message, objectMapper));
    }

    private String writeAsString(Object message, ObjectMapper objectMapper) {
        StringWriter sw = new StringWriter();
        try {
            objectMapper.writeValue(sw, message);
        } catch (IOException e) {
            LOG.error("Exception converting to JSON:", e);
        }
        return sw.toString();
    }
}
