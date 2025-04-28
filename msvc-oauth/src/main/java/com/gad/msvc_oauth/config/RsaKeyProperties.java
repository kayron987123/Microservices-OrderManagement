package com.gad.msvc_oauth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rsa.key")
@Getter
@Setter
public class RsaKeyProperties {
    private String publicKey;
    private String privateKey;
}