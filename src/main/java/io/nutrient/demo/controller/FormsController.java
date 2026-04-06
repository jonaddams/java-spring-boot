package io.nutrient.demo.controller;

import io.nutrient.demo.service.FormsService;
import io.nutrient.sdk.exceptions.NutrientException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/forms")
public class FormsController {

    private final FormsService formsService;

    public FormsController(FormsService formsService) {
        this.formsService = formsService;
    }

    @PostMapping("/list-fields")
    public ResponseEntity<?> listFields(@RequestParam("file") MultipartFile file) {
        try {
            var fields = formsService.listFormFields(file.getBytes());
            return ResponseEntity.ok(fields);
        } catch (NutrientException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
