package ru.avm.lib.stomp;

import ru.avm.lib.common.dto.AuthUserDto;

public class TrustStompSubscriptionAuthenticator implements StompSubscriptionAuthenticator {
    @Override
    public boolean authenticate(String destination, AuthUserDto principal) {
        return true;
    }
}
