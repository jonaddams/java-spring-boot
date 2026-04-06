package io.nutrient.demo.controller;

import io.nutrient.demo.service.EditorService;
import io.nutrient.sdk.exceptions.NutrientException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/editor")
public class EditorController {

    private final EditorService editorService;

    public EditorController(EditorService editorService) {
        this.editorService = editorService;
    }

    @PostMapping("/metadata")
    public ResponseEntity<String> getMetadata(@RequestParam("file") MultipartFile file) {
        try {
            String metadata = editorService.getMetadata(file.getBytes());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(metadata);
        } catch (NutrientException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
