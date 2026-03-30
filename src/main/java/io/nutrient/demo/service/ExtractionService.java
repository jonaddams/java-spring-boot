package io.nutrient.demo.service;

import io.nutrient.sdk.Document;
import io.nutrient.sdk.exceptions.NutrientException;
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
            try (Document document = Document.open(inputFile.toString())) {
                // Use VisionEngine.Ocr for fast OCR-only extraction
                // The exact extraction API will be verified in Task 9
                String extractedText = "";
                return Map.of(
                        "engine", "OCR",
                        "text", extractedText,
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
            try (Document document = Document.open(inputFile.toString())) {
                // Use VisionEngine.Icr for local ICR extraction (no VLM required)
                // Runs entirely offline using ONNX models
                String extractedText = "";
                return Map.of(
                        "engine", "ICR",
                        "text", extractedText,
                        "filename", originalFilename
                );
            }
        } finally {
            Files.deleteIfExists(inputFile);
        }
    }
}
