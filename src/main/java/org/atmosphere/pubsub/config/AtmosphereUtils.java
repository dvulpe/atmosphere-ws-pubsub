package org.atmosphere.pubsub.config;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Meteor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class AtmosphereUtils {


    public static AtmosphereResource<HttpServletRequest, HttpServletResponse> getAtmosphereResource(HttpServletRequest request) {
        return getMeteor(request).getAtmosphereResource();
    }

    public static Meteor getMeteor(HttpServletRequest request) {
        return Meteor.build(request);
    }
}
