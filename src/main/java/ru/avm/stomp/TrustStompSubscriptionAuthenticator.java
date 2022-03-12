package ru.avm.stomp;

import ru.avm.common.dto.AuthUserDto;

public class TrustStompSubscriptionAuthenticator implements StompSubscriptionAuthenticator {
    @Override
    public boolean authenticate(String destination, AuthUserDto principal) {
        return true;
    }
}
