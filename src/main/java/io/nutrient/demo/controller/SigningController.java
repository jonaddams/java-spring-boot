package io.nutrient.demo.controller;

import io.nutrient.demo.service.SigningService;
import io.nutrient.sdk.exceptions.NutrientException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/signing")
public class SigningController {

    private final SigningService signingService;

    public SigningController(SigningService signingService) {
        this.signingService = signingService;
    }

    @PostMapping("/sign")
    public ResponseEntity<byte[]> sign(
            @RequestParam("file") MultipartFile file,
            @RequestParam("certificate") MultipartFile certificate,
            @RequestParam(value = "password", defaultValue = "") String password) {
        try {
            byte[] result = signingService.signDocument(
                    file.getBytes(), certificate.getBytes(), password);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=signed.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(result);
        } catch (NutrientException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
