package io.nutrient.demo.controller;

import io.nutrient.demo.service.ExtractionService;
import io.nutrient.sdk.exceptions.NutrientException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/extraction")
public class ExtractionController {

    private final ExtractionService extractionService;

    public ExtractionController(ExtractionService extractionService) {
        this.extractionService = extractionService;
    }

    @PostMapping("/ocr")
    public ResponseEntity<Map<String, Object>> extractOcr(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> result = extractionService.extractTextOcr(
                    file.getBytes(), file.getOriginalFilename());
            return ResponseEntity.ok(result);
        } catch (NutrientException | IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/icr")
    public ResponseEntity<Map<String, Object>> extractIcr(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> result = extractionService.extractTextIcr(
                    file.getBytes(), file.getOriginalFilename());
            return ResponseEntity.ok(result);
        } catch (NutrientException | IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
