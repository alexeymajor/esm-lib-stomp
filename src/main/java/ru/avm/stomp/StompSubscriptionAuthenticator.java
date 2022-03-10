package ru.avm.stomp;


import ru.avm.common.dto.AuthUserDto;

public interface StompSubscriptionAuthenticator {
    boolean authenticate(String destination, AuthUserDto principal);
}
