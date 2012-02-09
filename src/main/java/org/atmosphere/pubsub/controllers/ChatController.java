package org.atmosphere.pubsub.controllers;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.pubsub.utils.AtmosphereUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class ChatController {

    @RequestMapping("/websockets")
    @ResponseBody
    public void subscribe(AtmosphereResource<HttpServletRequest, HttpServletResponse> resource) throws IOException {
        resource.getResponse().setContentType("text/html");
        AtmosphereUtils.suspend(resource);
    }

}
