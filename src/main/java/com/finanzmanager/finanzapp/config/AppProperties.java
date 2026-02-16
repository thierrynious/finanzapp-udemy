package com.finanzmanager.finanzapp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Liest alle Properties mit Prefix "app."
 * z.B. app.name, app.default-currency, app.max-transactions
 */
@Data // Lombok ersetzt Getter & Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String name;
    private String defaultCurrency;
    private int maxTransactions;
}
