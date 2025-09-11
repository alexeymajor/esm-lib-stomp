package ru.avm.lib.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@RequiredArgsConstructor

@Slf4j
@Configuration
@EnableWebSocketSecurity
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    private final StompProperties stompProperties;
    private final RabbitProperties rabbitProperties;
    private static final Integer STOMP_PORT = 61613;

    private final List<StompSubscriptionAuthenticator> authenticators;

    @Bean
    public AuthorizationManager<Message<?>> authorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages.simpDestMatchers("**").permitAll()

//                .simpDestMatchers("/admin/**").hasRole("ADMIN")
                .anyMessage().permitAll();
//        .authenticated();
        return messages.build();
    }

    @Bean
    public StompTopicSubscriptionInterceptor stompTopicSubscriptionInterceptor() {
        return new StompTopicSubscriptionInterceptor(authenticators);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompTopicSubscriptionInterceptor());
        registration.taskExecutor().corePoolSize(4).maxPoolSize(8);
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(stompProperties.endpoints.toArray(String[]::new))
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
