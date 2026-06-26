package com.abhisheksingh.loanaxisapi.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;


@Slf4j
@Configuration
public class RsaKeysConfig {
    @Bean
    public KeyPair keyPair() throws NoSuchAlgorithmException {
        log.info("🔐 Generating RSA key pair at runtime (development mode)");
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
}
