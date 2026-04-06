package io.nutrient.demo.controller;

import io.nutrient.demo.service.SigningService;
import io.nutrient.sdk.exceptions.NutrientException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;

@RestController
@RequestMapping("/api/signing")
public class SigningController {

    private final SigningService signingService;

    public SigningController(SigningService signingService) {
        this.signingService = signingService;
    }

    @GetMapping("/demo-certificate")
    public ResponseEntity<byte[]> getDemoCertificate() {
        try {
            ClassPathResource resource = new ClassPathResource("demo-certificate.p12");
            KeyStore ks = KeyStore.getInstance("PKCS12");
            try (InputStream is = resource.getInputStream()) {
                ks.load(is, "nutrient-demo".toCharArray());
            }
            String alias = ks.aliases().nextElement();
            Certificate cert = ks.getCertificate(alias);
            byte[] encoded = cert.getEncoded();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=demo-certificate.der")
                    .body(encoded);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/sign-demo")
    public ResponseEntity<byte[]> signDemo(@RequestParam("file") MultipartFile file) {
        try {
            byte[] result = signingService.signWithDemoCert(file.getBytes());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=signed.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(result);
        } catch (NutrientException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
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
