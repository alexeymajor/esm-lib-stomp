package ru.avm.lib.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import ru.avm.lib.common.dto.AuthUserDto;

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
        val headerAccessor = StompHeaderAccessor.wrap(message);

        if (!Objects.equals(headerAccessor.getCommand(), StompCommand.SUBSCRIBE)) {
            return message;
        }

        val destination = headerAccessor.getDestination();
        val principal = Optional.ofNullable(headerAccessor.getHeader(USER_HEADER))
                .map(o -> ((Authentication) o).getPrincipal())
                .orElse(null);

        val user = (principal instanceof AuthUserDto) ? (AuthUserDto) principal : null;

        try {
            val authenticated = authenticators.stream()
                    .anyMatch(authenticator -> authenticator.authenticate(destination, user));
            if (!authenticated) {
                log.warn("FAIL subscription: {}", headerAccessor);
                return null;
            }
        } catch (Exception e) {
            log.error("ERROR subscription: {}", headerAccessor, e);
            return null;
        }

        log.info("SUCCESS subscription: {}", headerAccessor);
        return message;
    }

}
