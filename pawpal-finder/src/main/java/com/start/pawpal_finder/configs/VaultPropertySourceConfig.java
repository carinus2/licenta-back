package com.start.pawpal_finder.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;
import org.springframework.context.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration for Vault Property Sources
 * Explicitly defines which Vault paths to read secrets from
 */
@Configuration
@Profile("vault")
public class VaultPropertySourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(VaultPropertySourceConfig.class);

    @EventListener
    public void onSecretLeaseCreated(SecretLeaseCreatedEvent event) {
        RequestedSecret requestedSecret = event.getSource();
        logger.info("Secret lease created for path: {}", requestedSecret.getPath());
    }
}
