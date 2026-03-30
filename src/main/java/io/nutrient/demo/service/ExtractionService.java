package io.nutrient.demo.service;

import io.nutrient.sdk.Document;
import io.nutrient.sdk.Vision;
import io.nutrient.sdk.enums.VisionEngine;
import io.nutrient.sdk.exceptions.NutrientException;
import io.nutrient.sdk.settings.SdkSettings;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Service
public class ExtractionService {

    public Map<String, Object> extractTextOcr(byte[] imageBytes, String originalFilename)
            throws NutrientException, IOException {
        Path inputFile = Files.createTempFile("input-", "-" + originalFilename);
        try {
            Files.write(inputFile, imageBytes);

            // Configure OCR engine — fast extraction, skips AI augmentation
            SdkSettings.getVisionSettings().setEngine(VisionEngine.Ocr);

            try (Document document = Document.open(inputFile.toString())) {
                Vision vision = Vision.set(document);
                String extractedContent = vision.extractContent();
                return Map.of(
                        "engine", "OCR",
                        "content", extractedContent,
                        "filename", originalFilename
                );
            }
        } finally {
            Files.deleteIfExists(inputFile);
        }
    }

    public Map<String, Object> extractTextIcr(byte[] imageBytes, String originalFilename)
            throws NutrientException, IOException {
        Path inputFile = Files.createTempFile("input-", "-" + originalFilename);
        try {
            Files.write(inputFile, imageBytes);

            // Configure ICR engine — local ONNX models, full layout analysis
            // Runs entirely offline, no external API calls required
            SdkSettings.getVisionSettings().setEngine(VisionEngine.Icr);

            try (Document document = Document.open(inputFile.toString())) {
                Vision vision = Vision.set(document);
                String extractedContent = vision.extractContent();
                return Map.of(
                        "engine", "ICR",
                        "content", extractedContent,
                        "filename", originalFilename
                );
            }
        } finally {
            Files.deleteIfExists(inputFile);
        }
    }
}
