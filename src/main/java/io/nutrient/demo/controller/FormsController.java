package io.nutrient.demo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nutrient.demo.service.FormsService;
import io.nutrient.sdk.exceptions.NutrientException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/forms")
public class FormsController {

    private final FormsService formsService;
    private final ObjectMapper objectMapper;

    public FormsController(FormsService formsService, ObjectMapper objectMapper) {
        this.formsService = formsService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/fill")
    public ResponseEntity<byte[]> fillForm(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fieldValues") String fieldValuesJson) {
        try {
            Map<String, String> fieldValues = objectMapper.readValue(
                    fieldValuesJson, new TypeReference<>() {});
            byte[] result = formsService.fillFormFields(file.getBytes(), fieldValues);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filled.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(result);
        } catch (NutrientException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create-fields")
    public ResponseEntity<byte[]> createFields(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fieldDefinitions") String fieldDefinitionsJson) {
        try {
            byte[] result = formsService.createFormFields(file.getBytes(), fieldDefinitionsJson);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=with-fields.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(result);
        } catch (NutrientException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
