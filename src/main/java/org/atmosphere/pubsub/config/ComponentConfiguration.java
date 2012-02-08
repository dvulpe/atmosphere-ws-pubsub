package org.atmosphere.pubsub.config;

import org.atmosphere.cpr.BroadcasterFactory;
import org.springframework.context.annotation.*;

@Configuration
@Import(WebAppConfiguration.class)
@ComponentScan(basePackages = "org.atmosphere.pubsub", excludeFilters = @ComponentScan.Filter(Configuration.class))
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ComponentConfiguration {

    @Bean
    public BroadcasterFactory broadcasterFactory() {
        return BroadcasterFactory.getDefault();
    }
}
