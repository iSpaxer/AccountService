package com.example.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

@Component
public class AppConfiguration {

    private final BuildProperties buildProperties;

    public static String APP_VERSION;

    public AppConfiguration(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @PostConstruct
    public void init() {
        APP_VERSION = getCleanVersion();
    }

    public String getVersion() {
        return buildProperties.getVersion();
    }


    public String getCleanVersion() {
        return buildProperties.getVersion().replace("-SNAPSHOT", ""); // ← "1.0.1"
    }
}
