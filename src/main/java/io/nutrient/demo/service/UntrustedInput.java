package io.nutrient.demo.service;

import io.nutrient.sdk.Document;
import io.nutrient.sdk.exceptions.NutrientException;
import io.nutrient.sdk.settings.ConversionSettings;
import io.nutrient.sdk.settings.DocumentSettings;

import java.nio.file.Path;

/**
 * Every upload is untrusted, and Document.open may render HTML-type content
 * (HTML, Markdown, email) through Chrome whatever the endpoint. SDK defaults
 * block only private-network access, so a page could still read local files,
 * reach the public internet and run scripts. Every service opens uploads here,
 * with all of that blocked, per Nutrient's HTML-to-PDF guide.
 * External images/CSS in uploaded HTML won't load; inline them instead.
 */
final class UntrustedInput {

    private UntrustedInput() {}

    static Document open(Path file) throws NutrientException {
        return Document.open(file.toString(), settings());
    }

    static DocumentSettings settings() {
        DocumentSettings settings = new DocumentSettings();
        ConversionSettings conversion = settings.getConversionSettings();
        conversion.setHtmlBlockNetworkAccess(true);
        conversion.setHtmlBlockPrivateNetworkAccess(true);
        conversion.setHtmlBlockFileAccess(true);
        conversion.setHtmlScriptsExecutionDisabled(true);
        conversion.setHtmlBlockWebSocketsAndWorkers(true);
        return settings;
    }
}
