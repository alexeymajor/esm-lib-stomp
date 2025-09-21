package ru.avm.lib.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import ru.avm.lib.security.TrustAuthenticationToken;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.USER_HEADER;

@RequiredArgsConstructor

@Slf4j
public class StompTopicSubscriptionInterceptor implements ChannelInterceptor {

    private final List<StompSubscriptionAuthenticator> authenticators;

    @Override
    @SuppressWarnings("NullableProblems")
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        if (!Objects.equals(headerAccessor.getCommand(), StompCommand.SUBSCRIBE)) {
            return message;
        }

        val destination = headerAccessor.getDestination();
        val principal = Optional.ofNullable(headerAccessor.getHeader(USER_HEADER))
                .map(o -> ((TrustAuthenticationToken) o).getPrincipal())
                .orElse(null);

        val authenticated = authenticators.stream()
                .anyMatch(authenticator -> authenticator.authenticate(destination, principal));

        if (!authenticated) {
            log.info("FAIL subscription: {}", headerAccessor);
            return null;
        }
        log.debug("SUCCESS subscription: {}", headerAccessor);
        return message;
    }


}
