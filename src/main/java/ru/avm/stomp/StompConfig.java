package ru.avm.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.util.List;

@RequiredArgsConstructor

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class StompConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    private final RabbitProperties rabbitProperties;
    private static final Integer STOMP_PORT = 61613;

    private final List<StompSubscriptionAuthenticator> authenticators;

    @Bean
    public StompTopicSubscriptionInterceptor stompTopicSubscriptionInterceptor() {
        return new StompTopicSubscriptionInterceptor(authenticators);
    }

    @Override
    protected void customizeClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompTopicSubscriptionInterceptor());
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.simpDestMatchers("**").permitAll(); //.authenticated();
        messages.anyMessage().permitAll(); //.authenticated();
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableStompBrokerRelay("/topic")
                .setRelayHost(rabbitProperties.determineHost())
                .setRelayPort(STOMP_PORT);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(4).maxPoolSize(8);
    }
}
