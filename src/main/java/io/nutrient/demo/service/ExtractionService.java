package io.nutrient.demo.service;

import io.nutrient.sdk.Document;
import io.nutrient.sdk.Vision;
import io.nutrient.sdk.enums.VisionEngine;
import io.nutrient.sdk.enums.VisionFeatures;
import io.nutrient.sdk.settings.VisionSettings;
import io.nutrient.sdk.exceptions.NutrientException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class ExtractionService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // SDK bug: native Close() on Vision objects SIGSEGV's on the GC Cleaner thread.
    // Retain references to prevent cleanup.
    private static final List<Vision> visionKeepAlive = new ArrayList<>();

    public Map<String, Object> extractTextOcr(byte[] imageBytes, String originalFilename)
            throws NutrientException, IOException {
        return extractWithEngine(imageBytes, originalFilename, "OCR");
    }

    public Map<String, Object> extractTextIcr(byte[] imageBytes, String originalFilename)
            throws NutrientException, IOException {
        return extractWithEngine(imageBytes, originalFilename, "ICR");
    }

    private Map<String, Object> extractWithEngine(byte[] imageBytes, String originalFilename, String engine)
            throws NutrientException, IOException {
        Path inputFile = Files.createTempFile("input-", "-" + originalFilename);
        try {
            Files.write(inputFile, imageBytes);
            try (Document document = Document.open(inputFile.toString())) {
                VisionSettings vs = document.getSettings().getVisionSettings();
                vs.setFeatures(VisionFeatures.Handwritting);

                VisionEngine visionEngine = "OCR".equals(engine) ? VisionEngine.Ocr : VisionEngine.Icr;
                vs.setEngine(visionEngine);

                Vision vision = Vision.set(document);
                visionKeepAlive.add(vision);
                String rawJson = vision.extractContent();
                return formatExtractionResult(rawJson, originalFilename, engine);
            }
        } finally {
            Files.deleteIfExists(inputFile);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> formatExtractionResult(String rawJson, String filename, String engine)
            throws IOException {
        Map<String, Object> parsed = objectMapper.readValue(rawJson,
                new TypeReference<Map<String, Object>>() {});

        List<Map<String, Object>> elements = (List<Map<String, Object>>) parsed.getOrDefault("elements", List.of());

        // Sort by readingOrder
        elements.sort(Comparator.comparingInt(e -> ((Number) e.getOrDefault("readingOrder", 0)).intValue()));

        // Build per-element text summaries with confidence
        List<Map<String, Object>> textElements = new ArrayList<>();
        StringBuilder fullText = new StringBuilder();

        for (Map<String, Object> element : elements) {
            String type = (String) element.getOrDefault("type", "");
            String text = (String) element.get("text");
            if (text == null || text.isBlank()) continue;

            Number confidence = (Number) element.getOrDefault("confidence", 0);
            int readingOrder = ((Number) element.getOrDefault("readingOrder", 0)).intValue();
            String role = (String) element.getOrDefault("role", "");

            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("readingOrder", readingOrder);
            summary.put("type", type);
            if (!role.isEmpty()) summary.put("role", role);
            summary.put("text", text);
            summary.put("confidence", Math.round(confidence.doubleValue() * 100.0) / 100.0);

            // Word-level detail with per-word confidence
            List<Map<String, Object>> words = (List<Map<String, Object>>) element.get("words");
            if (words != null) {
                List<Map<String, Object>> wordSummaries = new ArrayList<>();
                for (Map<String, Object> word : words) {
                    Map<String, Object> ws = new LinkedHashMap<>();
                    ws.put("text", word.get("text"));
                    Number wordConf = (Number) word.getOrDefault("confidence", 0);
                    ws.put("confidence", Math.round(wordConf.doubleValue() * 100.0) / 100.0);
                    ws.put("bounds", word.get("bounds"));
                    wordSummaries.add(ws);
                }
                summary.put("words", wordSummaries);
            }

            summary.put("bounds", element.get("bounds"));
            textElements.add(summary);

            if (!fullText.isEmpty()) fullText.append("\n");
            fullText.append("[").append(readingOrder).append("] ").append(text);
        }

        // Confidence statistics
        double avgConfidence = elements.stream()
                .filter(e -> e.get("confidence") != null)
                .mapToDouble(e -> ((Number) e.get("confidence")).doubleValue())
                .average()
                .orElse(0.0);
        long lowConfCount = elements.stream()
                .filter(e -> e.get("confidence") != null)
                .filter(e -> ((Number) e.get("confidence")).doubleValue() < 0.5)
                .count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalElements", elements.size());
        stats.put("textElements", textElements.size());
        stats.put("averageConfidence", Math.round(avgConfidence * 100.0) / 100.0);
        stats.put("lowConfidenceElements", lowConfCount);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("engine", engine);
        result.put("filename", filename);
        result.put("statistics", stats);
        result.put("fullText", fullText.toString());
        result.put("textElements", textElements);
        result.put("rawElements", elements);

        return result;
    }
}
