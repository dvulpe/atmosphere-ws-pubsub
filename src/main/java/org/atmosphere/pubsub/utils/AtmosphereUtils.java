package org.atmosphere.pubsub.utils;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.CountDownLatch;

public final class AtmosphereUtils {


    public static final Logger LOG = LoggerFactory.getLogger(AtmosphereUtils.class);

    public static AtmosphereResource<HttpServletRequest, HttpServletResponse> getAtmosphereResource(HttpServletRequest request) {
        return getMeteor(request).getAtmosphereResource();
    }

    public static Meteor getMeteor(HttpServletRequest request) {
        return Meteor.build(request);
    }

    public static void suspend(final AtmosphereResource<HttpServletRequest, HttpServletResponse> resource) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        resource.addEventListener(new AtmosphereResourceEventListenerAdapter() {
            @Override
            public void onSuspend(AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event) {
                countDownLatch.countDown();
                resource.removeEventListener(this);
            }
        });
        resource.suspend();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            LOG.error("Interrupted while trying to suspend resource {}", resource);
        }
    }
}
