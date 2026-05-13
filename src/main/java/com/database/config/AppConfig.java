package com.database.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

    @Value("${api.version.v1}")
    private String v1;

    public String getV1() {
        return v1;
    }
}
