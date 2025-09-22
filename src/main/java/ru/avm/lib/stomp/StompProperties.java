package ru.avm.lib.stomp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "app.stomp")
public class StompProperties {
    boolean disabled = false;
    List<String> endpoints = new ArrayList<>() {{
        add("/stomp");
        add("/stomp/");
    }};
}
