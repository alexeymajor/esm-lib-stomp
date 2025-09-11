package ru.avm.lib.stomp;


import ru.avm.lib.common.dto.AuthUserDto;

public interface StompSubscriptionAuthenticator {
    boolean authenticate(String destination, AuthUserDto principal);
}
