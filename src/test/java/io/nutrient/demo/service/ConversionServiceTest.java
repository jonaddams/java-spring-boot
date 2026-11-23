package io.nutrient.demo.service;

import io.nutrient.sdk.settings.ConversionSettings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConversionServiceTest {

    // Uploaded HTML is untrusted. Each flag below closes a hole the SDK leaves
    // open by default (network, files, scripts); dropping one must fail here.
    @Test
    void untrustedInputSettings_lockDownHtmlRendering() {
        ConversionSettings c = ConversionService.untrustedInputSettings().getConversionSettings();
        assertTrue(c.getHtmlBlockNetworkAccess(), "network");
        assertTrue(c.getHtmlBlockPrivateNetworkAccess(), "private network");
        assertTrue(c.getHtmlBlockFileAccess(), "file access");
        assertTrue(c.getHtmlScriptsExecutionDisabled(), "scripts");
        assertTrue(c.getHtmlBlockWebSocketsAndWorkers(), "websockets/workers");
    }
}
