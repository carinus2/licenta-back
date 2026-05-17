package com.start.pawpal_finder.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;

/**
 * Configuration class for HashiCorp Vault integration
 * This class provides centralized configuration and monitoring for Vault connectivity
 */
@Configuration
@Profile("vault")
public class VaultConfig {

    @Value("${spring.cloud.vault.uri}")
    private String vaultUri;

    @Value("${spring.cloud.vault.kv.backend}")
    private String kvBackend;

    @Value("${spring.cloud.vault.kv.default-context}")
    private String defaultContext;

    @Value("${spring.application.name}")
    private String applicationName;

    @PostConstruct
    public void init() {
        System.out.println("=================================================");
        System.out.println("HashiCorp Vault Configuration Initialized");
        System.out.println("=================================================");
        System.out.println("Vault URI: " + vaultUri);
        System.out.println("KV Backend: " + kvBackend);
        System.out.println("Default Context: " + defaultContext);
        System.out.println("Application Name: " + applicationName);
        System.out.println("=================================================");
        System.out.println("Secrets will be loaded from: " + kvBackend + "/data/" + defaultContext + "/*");
        System.out.println("=================================================");
    }

    public String getVaultUri() {
        return vaultUri;
    }

    public String getKvBackend() {
        return kvBackend;
    }

    public String getDefaultContext() {
        return defaultContext;
    }

    public String getApplicationName() {
        return applicationName;
    }
}