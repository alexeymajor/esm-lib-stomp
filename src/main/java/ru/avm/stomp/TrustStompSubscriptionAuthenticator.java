package ru.avm.stomp;

import ru.avm.common.dto.AuthUserDto;
import ru.avm.stomp.StompSubscriptionAuthenticator;

public class TrustStompSubscriptionAuthenticator implements StompSubscriptionAuthenticator {
    @Override
    public boolean authenticate(String destination, AuthUserDto principal) {
        return true;
    }
}
