package io.nutrient.demo.config;

import io.nutrient.sdk.License;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NutrientConfig {

    private static final Logger log = LoggerFactory.getLogger(NutrientConfig.class);

    @Value("${nutrient.license-key:}")
    private String licenseKey;

    @PostConstruct
    public void initializeSdk() {
        if (licenseKey != null && !licenseKey.isBlank()) {
            License.registerKey(licenseKey);
            log.info("Nutrient SDK license key registered");
        } else {
            log.warn("No Nutrient SDK license key configured — running in trial mode");
        }
    }
}
